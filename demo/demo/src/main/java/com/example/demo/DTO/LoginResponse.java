package com.example.demo.DTO;

import com.example.demo.Model.UserModel;

public class LoginResponse {
    private String id;
    private String fullName;
    private String email;
    private UserModel.Role role;
    private UserModel.Status status;

    public LoginResponse() {}

    public LoginResponse(String id, String fullName, String email, UserModel.Role role, UserModel.Status status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public UserModel.Role getRole() { return role; }
    public UserModel.Status getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(UserModel.Role role) { this.role = role; }
    public void setStatus(UserModel.Status status) { this.status = status; }
}
