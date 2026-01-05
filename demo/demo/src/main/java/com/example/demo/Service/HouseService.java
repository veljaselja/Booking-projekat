package com.example.demo.Service;

import com.example.demo.Model.HouseModel;

import java.util.List;
import java.time.LocalDate;


public interface HouseService {
    HouseModel createHouse(String hostId, HouseModel house);
    // HOST kreira -> PENDING
    HouseModel updateHouse(String hostId, String houseId, HouseModel patch);
    void deleteHouse(String hostId, String houseId);

    List<HouseModel> getMyHouses(String hostId);
    List<HouseModel> getPendingHouses();                             // ADMIN
    HouseModel approveHouse(String adminId, String houseId);         // ADMIN
    HouseModel rejectHouse(String adminId, String houseId);          // ADMIN

    // javno/browse
    List<HouseModel> browseApproved(String city);
    HouseModel getApprovedById(String houseId);
    List<HouseModel> searchAvailable(String city, Integer guests, LocalDate from, LocalDate to);

}
