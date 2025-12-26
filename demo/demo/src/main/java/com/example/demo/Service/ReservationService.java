package com.example.demo.Service;

import com.example.demo.Model.ReservationModel;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationModel requestReservation(String guestId, ReservationModel req);

    ReservationModel approveReservation(String actorId, String reservationId, String note);

    ReservationModel rejectReservation(String actorId, String reservationId, String note);

    ReservationModel cancelReservation(String guestId, String reservationId);

    boolean isHouseAvailable(String houseId, LocalDate from, LocalDate to);

    List<ReservationModel> getByGuest(String guestId);

    List<ReservationModel> getByHouse(String houseId);

    ReservationModel getById(String reservationId);
}
