package com.example.backend.mapper;

import com.example.backend.entity.User;
import com.example.backend.models.request.UserRequest;
import com.example.backend.models.response.UserResponse;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toEntity(UserRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    public static UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static void updateEntity(User user, UserRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
    }
}