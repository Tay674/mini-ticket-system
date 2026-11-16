package com.example.miniticketsystem.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,UserRepository userRepository) {
        this.userService = userService;
		this.userRepository = userRepository;
        
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User createdUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", createdUser.getId(),
                "username", createdUser.getUsername(),
                "email", createdUser.getEmail()
        ));
    }
    
  
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        var users = userRepository.findAll().stream()
                .map(u -> Map.of(
                        "id", u.getId(), 
                        "username", u.getUsername(),
                        "role", u.getRole().name() // Wir geben die Rolle mit aus!
                ))
                .toList();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeRole(@PathVariable java.util.UUID id, @RequestParam com.example.miniticketsystem.user.Role role) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Rolle geändert zu " + role));
    }
}