package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.DuplicateUserEmailException;
import com.shahkabir.coffer.exception.InvalidCredentialsException;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.model.User;
import com.shahkabir.coffer.model.enums.Role;
import com.shahkabir.coffer.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       UserService userService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(
            String email,
            String rawPassword,
            Customer customer
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserEmailException(
                    "A user with this email already exists"
            );
        }

        return userService.createUser(
                email,
                rawPassword,
                Role.CUSTOMER,
                customer
        );
    }

    public User authenticate(
            String email,
            String rawPassword
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                rawPassword,
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"

            );
        }
        return user;
    }
}
