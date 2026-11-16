package com.example.miniticketsystem.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRegistrationRequest(
    @NotBlank(message = "Username darf nicht leer sein")
    @Size(min = 3, max = 50, message = "Username muss zwischen 3 und 50 Zeichen lang sein")
    String username,

    @NotBlank(message = "Email darf nicht leer sein")
    @Email(message = "Ungültiges Email-Format")
    String email,

    @NotBlank(message = "Passwort darf nicht leer sein")
    @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
    String password
) {}