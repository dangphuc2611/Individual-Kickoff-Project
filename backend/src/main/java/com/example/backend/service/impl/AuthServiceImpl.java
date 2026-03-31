package com.example.backend.service.impl;

import com.example.backend.entity.DonVi;
import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.models.request.LoginRequest;
import com.example.backend.models.request.RegisterRequest;
import com.example.backend.models.response.AuthResponse;
import com.example.backend.repository.DonViRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final DonViRepository donViRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .donViId(userDetails.getDonViId())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng: " + request.getEmail());
        }

        DonVi donVi = null;
        if (request.getDonViId() != null) {
            donVi = donViRepository.findById(request.getDonViId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn vị ID: " + request.getDonViId()));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDonVi(donVi);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        return AuthResponse.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .role(saved.getRole())
                .donViId(donVi != null ? donVi.getId() : null)
                .build();
    }
}
