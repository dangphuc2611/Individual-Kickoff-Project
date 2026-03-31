package com.example.backend.controller;

import com.example.backend.models.request.DonViRequest;
import com.example.backend.models.response.DonViResponse;
import com.example.backend.service.DonViService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/don-vi")
@RequiredArgsConstructor
public class DonViController {

    private final DonViService donViService;

    @GetMapping
    public ResponseEntity<List<DonViResponse>> getAll() {
        return ResponseEntity.ok(donViService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonViResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donViService.getById(id));
    }

    @PostMapping
    public ResponseEntity<DonViResponse> create(@Valid @RequestBody DonViRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donViService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonViResponse> update(@PathVariable Long id, @Valid @RequestBody DonViRequest request) {
        return ResponseEntity.ok(donViService.update(id, request));
    }
}
