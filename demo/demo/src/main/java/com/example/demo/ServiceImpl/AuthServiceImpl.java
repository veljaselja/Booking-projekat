package com.example.demo.ServiceImpl;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.Exception.ApiException;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;

    public AuthServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new ApiException("Email je obavezan.");

        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new ApiException("Lozinka je obavezna.");

        UserModel user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("Pogrešan email ili lozinka."));

        if (user.getPasswordHash() == null || !user.getPasswordHash().equals(req.getPassword())) {
            throw new ApiException("Pogrešan email ili lozinka.");
        }


        if (user.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Nalog nije odobren. Status: " + user.getStatus());
        }

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
