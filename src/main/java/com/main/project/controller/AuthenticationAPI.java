package com.main.project.controller;

import com.main.project.dto.request.LoginRequest;
import com.main.project.dto.request.RegisterRequest;
import com.main.project.dto.response.LoginResponse;
import com.main.project.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/authentication")
//@SecurityRequirement(name = "api")
@Tag(name = "Authentication API", description = "API for authentication")
public class AuthenticationAPI {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String notify = authenticationService.register(registerRequest);
        return ResponseEntity.ok(notify);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
