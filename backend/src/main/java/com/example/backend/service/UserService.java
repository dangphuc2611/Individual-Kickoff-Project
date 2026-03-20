package com.example.backend.service;

import java.util.List;

import com.example.backend.models.request.UserRequest;
import com.example.backend.models.response.UserResponse;

import jakarta.validation.Valid;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, @Valid UserRequest request);

    void deleteUser(Long id);

    UserResponse createUser(@Valid UserRequest request);
}