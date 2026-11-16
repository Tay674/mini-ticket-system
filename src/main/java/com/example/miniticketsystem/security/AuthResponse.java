package com.example.miniticketsystem.security;

public record AuthResponse(String token, String role, String username) {}