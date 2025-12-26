package com.example.demo.Repository;

import com.example.demo.Model.HouseModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HouseRepository extends MongoRepository<HouseModel, String> {
    List<HouseModel> findByOwnerId(String ownerId);
    List<HouseModel> findByIsActiveTrue();
}
