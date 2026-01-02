package com.example.demo.Service;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req);
}
