package com.example.demo.Controller;

import com.example.demo.Model.UserModel;
import com.example.demo.Service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registracija HOST/GUEST (status ide PENDING)
    @PostMapping("/register")
    public UserModel register(@RequestBody UserModel user) {
        return userService.register(user);
    }

    // (opciono) dohvat pending korisnika - realno admin endpoint,
    @GetMapping("/pending")
    public List<UserModel> pending() {
        return userService.getPendingUsers();
    }

    @GetMapping("/{id}")
    public UserModel getById(@PathVariable String id) {
        return userService.getById(id);
    }
}
