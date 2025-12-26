package com.example.demo.Repository;

import com.example.demo.Model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserModel> findByStatus(UserModel.Status status);

    List<UserModel> findByRole(UserModel.Role role);
}
