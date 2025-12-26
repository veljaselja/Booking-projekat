package com.example.demo.Repository;

import com.example.demo.Model.RatingCommentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingCommentRepository extends MongoRepository<RatingCommentModel, String> {

    List<RatingCommentModel> findByHouseId(String houseId);

    List<RatingCommentModel> findByGuestId(String guestId);

    Optional<RatingCommentModel> findByHouseIdAndGuestId(String houseId, String guestId);

    boolean existsByHouseIdAndGuestId(String houseId, String guestId);
}
