package com.example.miniticketsystem.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username ist bereits vergeben.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email wird bereits verwendet.");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        
        User newUser = new User(
                request.username(),
                request.email(),
                hashedPassword
        );
        return userRepository.save(newUser);
    }
}