package com.yashrajput.shieldgate.controller;

import com.yashrajput.shieldgate.dto.AuthResponse;
import com.yashrajput.shieldgate.dto.LoginRequest;
import com.yashrajput.shieldgate.dto.RegisterRequest;
import com.yashrajput.shieldgate.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}