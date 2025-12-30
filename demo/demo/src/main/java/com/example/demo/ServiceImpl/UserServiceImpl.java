package com.example.demo.ServiceImpl;

import com.example.demo.Exception.ApiException;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;

@Service

public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserModel register(UserModel user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ApiException("Email je obavezan.");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new ApiException("Email je već zauzet.");
        }


        // viewer se ne registruje; samo ADMIN/HOST/GUEST
        if (user.getRole() == null) throw new ApiException("Role je obavezan.");

        user.setStatus(UserModel.Status.PENDING);
        user.setCreatedAt(Instant.now());

        
        return userRepo.save(user);
    }

    @Override
    public UserModel approveUser(String adminId, String userId) {
        UserModel admin = getById(adminId);
        if (admin.getRole() != UserModel.Role.ADMIN) throw new ApiException("Samo ADMIN može da odobri korisnika.");

        UserModel u = getById(userId);
        if (u.getStatus() != UserModel.Status.PENDING) throw new ApiException("Korisnik nije u PENDING statusu.");

        u.setStatus(UserModel.Status.APPROVED);
        u.setApprovedAt(Instant.now());
        u.setApprovedByAdminId(adminId);

        return userRepo.save(u);
    }

    @Override
    public UserModel disableUser(String adminId, String userId) {
        UserModel admin = getById(adminId);
        if (admin.getRole() != UserModel.Role.ADMIN) throw new ApiException("Samo ADMIN može da ukloni/disable korisnika.");

        UserModel u = getById(userId);
        u.setStatus(UserModel.Status.DISABLED);
        return userRepo.save(u);
    }

    @Override
    public List<UserModel> getPendingUsers() {
        return userRepo.findByStatus(UserModel.Status.PENDING);
    }

    @Override
    public UserModel getByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException("Korisnik sa email-om ne postoji."));
    }

    @Override
    public UserModel getById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ApiException("Korisnik ne postoji."));
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        if (req == null) throw new ApiException("Body je obavezan.");
        if (req.getEmail() == null || req.getEmail().isBlank()) throw new ApiException("Email je obavezan.");
        if (req.getPassword() == null || req.getPassword().isBlank()) throw new ApiException("Password je obavezan.");

        UserModel user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("Pogrešan email ili lozinka."));

        // status provera
        if (user.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Nalog nije odobren. Status: " + user.getStatus());
        }
        if (user.getStatus() == UserModel.Status.DISABLED) {
            throw new ApiException("Nalog je onemogućen.");
        }

        if (!user.getPasswordHash().equals(req.getPassword())) {
            throw new ApiException("Pogrešan email ili lozinka.");
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
