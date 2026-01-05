package com.example.demo.Controller;

import com.example.demo.Model.UserModel;
import com.example.demo.Model.HouseModel;
import com.example.demo.Service.UserService;
import com.example.demo.Service.HouseService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UserService userService;
    private final HouseService houseService;

    public AdminController(UserService userService, HouseService houseService) {
        this.userService = userService;
        this.houseService = houseService;     // ✅
    }

    // ===== USERS =====

    @GetMapping("/users/pending")
    public List<UserModel> getPendingUsers() {
        return userService.getPendingUsers();
    }

    @PostMapping("/users/{userId}/approve")
    public UserModel approveUser(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String userId) {
        return userService.approveUser(adminId, userId);
    }

    @PostMapping("/users/{userId}/disable")
    public UserModel disableUser(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String userId) {
        return userService.disableUser(adminId, userId);
    }

    // ===== HOUSES =====

    @GetMapping("/houses/pending")
    public List<HouseModel> getPendingHouses() {
        return houseService.getPendingHouses();
    }

    @PostMapping("/houses/{houseId}/approve")
    public HouseModel approveHouse(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String houseId) {
        return houseService.approveHouse(adminId, houseId);
    }

    @PostMapping("/houses/{houseId}/reject")
    public HouseModel rejectHouse(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String houseId) {
        return houseService.rejectHouse(adminId, houseId);
    }
}
