package com.shahkabir.coffer.controller;

import com.shahkabir.coffer.dto.LoginRequest;
import com.shahkabir.coffer.dto.LoginResponse;
import com.shahkabir.coffer.dto.RegisterRequest;
import com.shahkabir.coffer.dto.RegisterResponse;
import com.shahkabir.coffer.model.User;
import com.shahkabir.coffer.model.enums.Role;
import com.shahkabir.coffer.service.AuthService;
import com.shahkabir.coffer.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService,
                          TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
            ) {
        User user = authService.register(
                request.email(),
                request.rawPassword(),
                request.customer()
        );

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCustomer() != null
                ? user.getCustomer().getId()
                        : null
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        User user = authService.authenticate(
                request.email(),
                request.rawPassword()
        );

        String token = tokenService.generateToken(user);

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCustomer() == null
                        ? null
                        : user.getCustomer().getId(),
                token,
                "Bearer",
                900
        );

        return ResponseEntity.ok(response);
    }
}
