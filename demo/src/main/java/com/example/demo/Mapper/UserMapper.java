package com.example.demo.Mapper;

import com.example.demo.Entity.User;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    @Mapping(target = "lastName", ignore = true)
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
