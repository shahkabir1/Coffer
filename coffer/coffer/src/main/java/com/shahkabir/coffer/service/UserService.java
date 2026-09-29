package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.UserNotFoundException;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.model.User;
import com.shahkabir.coffer.model.enums.Role;
import com.shahkabir.coffer.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String email,
            String rawPassword,
            Role role,
            Customer customer
    ) {

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(
                email,
                passwordHash,
                role,
                customer
        );

        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }
}
