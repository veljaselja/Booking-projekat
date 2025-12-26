package com.example.demo.Repository;

import com.example.demo.Model.ReservationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends MongoRepository<ReservationModel, String> {

    List<ReservationModel> findByGuestId(String guestId);

    List<ReservationModel> findByHouseId(String houseId);

    List<ReservationModel> findByHouseIdAndStatus(String houseId, ReservationModel.Status status);

    // overlap check for APPROVED reservations:
    // existing.dateFrom <= to AND existing.dateTo >= from
    List<ReservationModel> findByHouseIdAndStatusAndDateFromLessThanEqualAndDateToGreaterThanEqual(
            String houseId,
            ReservationModel.Status status,
            LocalDate to,
            LocalDate from
    );
}
