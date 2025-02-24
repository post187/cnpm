package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Enum.Role;
import com.example.demo.Exception.AppException;
import com.example.demo.Exception.ErrorCode;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Respository.UserRepository;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    public User createUser(UserCreationRequest request) {

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("User exist");
            }

            User user = userMapper.toUser(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            HashSet<String> roles = new HashSet<>();
            roles.add(Role.USER.name());
            user.setRoles(roles);
            return userRepository.save(user);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        log.info("In method get User");
      return  userRepository.findAll();
    }

    @PostAuthorize("returnObject.username == authentication.username")
    public UserResponse getUser(String id) {
        log.info("In method get User by Id");
        return  userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
}
