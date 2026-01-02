package com.example.demo;

import com.example.demo.Model.UserModel;
import com.example.demo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Bean
//	CommandLineRunner seedAdmin(UserRepository userRepo) {
//		return args -> {
//			String adminEmail = "admin@booking.com";
//
//			if (!userRepo.existsByEmail(adminEmail)) {
//				UserModel admin = new UserModel();
//				admin.setFullName("Admin");
//				admin.setEmail(adminEmail);
//				admin.setPasswordHash("admin123");
//				admin.setRole(UserModel.Role.ADMIN);
//				admin.setStatus(UserModel.Status.APPROVED);
//				admin.setCreatedAt(Instant.now());
//
//				userRepo.save(admin);
//
//				System.out.println("Admin kreiran: " + adminEmail);
//			}
//		};
//	}

	CommandLineRunner printDb(org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
		return args -> {
			System.out.println("CONNECTED TO DB: " + mongoTemplate.getDb().getName());
		};
	}


}
