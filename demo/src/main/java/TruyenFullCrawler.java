import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TruyenFullCrawler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập link truyện: ");
        String url = scanner.nextLine();
        scanner.close();

        try {
            crawlTruyen(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void crawlTruyen(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String title = doc.select("h3.title").text().replaceAll("[\\/:*?\"<>|]", "_");
        System.out.println("Truyện: " + title);

        // Lấy tác giả
        String author = doc.select("a[itemprop='author']").text();
        if (author.isEmpty()) {
            author = "Không rõ";
        }

        //Lấy thể loại
        Elements genreElements = doc.select(".info a[itemprop='genre']");
        List<String> genres = new ArrayList<>();
        for (Element e : genreElements) {
            genres.add(e.text());
        }
        String category = genres.isEmpty() ? "None" : String.join(", ", genres);

        // Lấy ảnh bìa
        String coverImageUrl = doc.select(".book img").attr("src");

        // Lấy phần giới thiệu
        String description = doc.select("div.desc-text p").text().replaceAll("&nbsp;", "\n");
        description = description.replaceAll("(?i)^Tác giả:.*?Giới thiệu.*?", "");

        // Tạo thư mục nếu chưa tồn tại
        File folder = new File("truyen/" + title);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Lưu thông tin truyện vào metadata.txt
        saveMetadata(title, author, category, coverImageUrl, description);

        Elements chapters = doc.select("ul.list-chapter li a");
        List<String> chapterLinks = new ArrayList<>();

        // Kiểm tra chương đã tồn tại
        File[] existingFiles = folder.listFiles();
        List<String> existingChapters = new ArrayList<>();

        if (existingFiles != null) {
            for (File file : existingFiles) {
                existingChapters.add(file.getName().replace(".txt", ""));
            }
        }

        for (Element chapter : chapters) {
            String chapterTitle = chapter.text().replaceAll("[\\/:*?\"<>|]", "_");
            if (!existingChapters.contains(chapterTitle)) {
                chapterLinks.add(chapter.absUrl("href"));
            }
        }

        if (chapterLinks.isEmpty()) {
            System.out.println("Không có chương mới nào.");
            return;
        }

        for (String chapterUrl : chapterLinks) {
            crawlChapter(chapterUrl, title);
        }

        System.out.println("Đã cập nhật chương mới cho truyện: " + title);
    }

    public static void crawlChapter(String chapterUrl, String title) {
        try {
            Document doc = Jsoup.connect(chapterUrl).get();
            String chapterTitle = doc.select("h2").text().replaceAll("[\\/:*?\"<>|]", "_");
            String content = doc.select("div#chapter-c").html()
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("<br\\s*/?>", "\n")
                    .replaceAll("</p>", "\n\n")
                    .replaceAll("<[^>]+>", "")
                    .replaceAll("\\n{2,}", "\n");

            System.out.println("Tải: " + chapterTitle);
            saveToFile(title, chapterTitle, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToFile(String title, String chapterTitle, String content) {
        try {
            File file = new File("truyen/" + title + "/" + chapterTitle + ".txt");
            FileWriter writer = new FileWriter(file);
            writer.write(content.trim());
            writer.close();
            System.out.println("Đã lưu: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveMetadata(String title, String author, String category, String coverImageUrl, String description) {
        try {
            File file = new File("truyen/" + title + "/metadata.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("Tên truyện: " + title + "\n");
            writer.write("Tác giả: " + author + "\n");
            writer.write("Thể loại: " + category + "\n");
            writer.write("Ảnh bìa: " + coverImageUrl + "\n");
            writer.write("Giới thiệu:" + description + "\n");
            writer.close();
            System.out.println("Đã lưu metadata.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
