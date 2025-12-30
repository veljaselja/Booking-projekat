package com.example.demo.Service;

import com.example.demo.Model.UserModel;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import java.util.List;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;

public interface UserService {
    UserModel register(UserModel user);                     // status=PENDING
    UserModel approveUser(String adminId, String userId);   // status=APPROVED
    UserModel disableUser(String adminId, String userId);   // status=DISABLED

    LoginResponse login(LoginRequest req);


    List<UserModel> getPendingUsers();
    UserModel getByEmail(String email);
    UserModel getById(String id);
}
