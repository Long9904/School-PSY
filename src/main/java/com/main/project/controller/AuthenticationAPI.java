package com.main.project.controller;

import com.main.project.dto.request.RegisterRequest;
import com.main.project.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/authentication")
//@SecurityRequirement(name = "api")
public class AuthenticationAPI {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String notify = authenticationService.register(registerRequest);
        return ResponseEntity.ok(notify);
    }

    @GetMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok("Login successful");
    }
}
