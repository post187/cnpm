package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName; // kieu mac dinh la private.



}
