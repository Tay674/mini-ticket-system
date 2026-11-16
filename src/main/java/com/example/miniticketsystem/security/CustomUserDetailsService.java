package com.example.miniticketsystem.security;

import com.example.miniticketsystem.user.User;
import com.example.miniticketsystem.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User nicht gefunden: " + username));

        // Rolle aus der Datenbank in eine Spring Security Authority umwandeln (z.B. ROLE_ADMIN)
        org.springframework.security.core.authority.SimpleGrantedAuthority authority = 
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                java.util.List.of(authority) // Wir übergeben die Rolle hier!
        );
    }
}