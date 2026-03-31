package com.example.backend.service;

import com.example.backend.models.request.LoginRequest;
import com.example.backend.models.request.RegisterRequest;
import com.example.backend.models.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
