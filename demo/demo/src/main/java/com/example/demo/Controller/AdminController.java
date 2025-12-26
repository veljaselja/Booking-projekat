package com.example.demo.Controller;

import com.example.demo.Model.UserModel;
import com.example.demo.Service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/pending")
    public List<UserModel> getPendingUsers() {
        return userService.getPendingUsers();
    }

    @PostMapping("/users/{userId}/approve")
    public UserModel approveUser(@RequestParam String adminId, @PathVariable String userId) {
        return userService.approveUser(adminId, userId);
    }

    @PostMapping("/users/{userId}/disable")
    public UserModel disableUser(@RequestParam String adminId, @PathVariable String userId) {
        return userService.disableUser(adminId, userId);
    }
}
