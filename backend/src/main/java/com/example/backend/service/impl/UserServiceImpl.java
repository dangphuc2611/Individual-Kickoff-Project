package com.example.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.models.request.UserRequest;
import com.example.backend.models.response.UserResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.DonViRepository;
import com.example.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DonViRepository donViRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required for new user");
        }

        User user = UserMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getDonViId() != null) {
            user.setDonVi(donViRepository.findById(request.getDonViId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn vị ID: " + request.getDonViId())));
        }

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));

        UserMapper.updateEntity(user, request);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        if (request.getDonViId() != null) {
            user.setDonVi(donViRepository.findById(request.getDonViId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn vị ID: " + request.getDonViId())));
        } else {
            user.setDonVi(null);
        }

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));

        userRepository.delete(user);
    }
}