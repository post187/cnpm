package com.example.demo.Controller;

import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import com.example.demo.dto.request.APIResponse;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    APIResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        APIResponse<User> apiResponse = new APIResponse<>();

        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    List<User> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}" + authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return userService.getUsers();
    }
    @GetMapping("/{UserId}")
    UserResponse getUser(@PathVariable("UserId") String userId){
        return userService.getUser(userId);
    }
    @PutMapping("/{UserId}")
    UserResponse updateUser(@PathVariable("UserId") String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }
    @DeleteMapping("/{UserId}")
    String deleteUser(@PathVariable("UserId") String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    @GetMapping("/myInfo")
    UserResponse getMyInfo(){
        return userService.getMyInfo();
    }
}
