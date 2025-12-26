package com.example.demo.Service;

import com.example.demo.Model.UserModel;

import java.util.List;
public interface UserService {
    UserModel register(UserModel user);                     // status=PENDING
    UserModel approveUser(String adminId, String userId);   // status=APPROVED
    UserModel disableUser(String adminId, String userId);   // status=DISABLED

    List<UserModel> getPendingUsers();
    UserModel getByEmail(String email);
    UserModel getById(String id);
}
