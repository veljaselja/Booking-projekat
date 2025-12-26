package com.example.demo.Service;

import com.example.demo.Model.HouseModel;

import java.util.List;
public interface HouseService {

    HouseModel createHouse(String ownerId, HouseModel house);

    HouseModel updateHouse(String actorId, String houseId, HouseModel updates);

    HouseModel deactivateHouse(String actorId, String houseId);

    HouseModel activateHouse(String actorId, String houseId);

    HouseModel getById(String houseId);

    List<HouseModel> getActiveHouses();      // za VIEWER (anonimno)

    List<HouseModel> getHousesByOwner(String ownerId); // za HOST
}


