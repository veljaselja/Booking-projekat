package com.example.demo.DTO;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {}
