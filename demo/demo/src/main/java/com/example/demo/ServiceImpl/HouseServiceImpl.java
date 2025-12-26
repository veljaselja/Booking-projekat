package com.example.demo.ServiceImpl;

import com.example.demo.Exception.ApiException;
import com.example.demo.Model.HouseModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.HouseRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.HouseService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepo;
    private final UserRepository userRepo;

    public HouseServiceImpl(HouseRepository houseRepo, UserRepository userRepo) {
        this.houseRepo = houseRepo;
        this.userRepo = userRepo;
    }

    @Override
    public HouseModel createHouse(String ownerId, HouseModel house) {
        UserModel owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new ApiException("Owner ne postoji."));

        // HOST ili ADMIN može kreirati
        if (owner.getRole() != UserModel.Role.HOST && owner.getRole() != UserModel.Role.ADMIN) {
            throw new ApiException("Samo HOST (ili ADMIN) može da kreira objekat.");
        }

        // Mora biti odobren nalog
        if (owner.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Korisnik nije odobren.");
        }

        // Minimalne validacije
        if (house.getTitle() == null || house.getTitle().isBlank()) throw new ApiException("Naslov je obavezan.");
        if (house.getCity() == null || house.getCity().isBlank()) throw new ApiException("Grad je obavezan.");
        if (house.getAddress() == null || house.getAddress().isBlank()) throw new ApiException("Adresa je obavezna.");
        if (house.getPricePerNight() <= 0) throw new ApiException("Cena po noćenju mora biti > 0.");
        if (house.getMaxGuests() <= 0) throw new ApiException("Max guests mora biti > 0.");

        house.setOwnerId(ownerId);
        house.setActive(true);
        house.setCreatedAt(Instant.now());

        return houseRepo.save(house);
    }

    @Override
    public HouseModel updateHouse(String actorId, String houseId, HouseModel updates) {
        HouseModel existing = getById(houseId);
        UserModel actor = userRepo.findById(actorId)
                .orElseThrow(() -> new ApiException("Korisnik ne postoji."));

        boolean isAdmin = actor.getRole() == UserModel.Role.ADMIN;
        boolean isOwner = existing.getOwnerId() != null && existing.getOwnerId().equals(actorId);

        if (!isAdmin && !isOwner) {
            throw new ApiException("Nemaš pravo da menjaš ovaj objekat.");
        }

        // Update samo ako je poslato (partial update)
        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getCity() != null) existing.setCity(updates.getCity());
        if (updates.getAddress() != null) existing.setAddress(updates.getAddress());
        if (updates.getPricePerNight() > 0) existing.setPricePerNight(updates.getPricePerNight());
        if (updates.getMaxGuests() > 0) existing.setMaxGuests(updates.getMaxGuests());
        if (updates.getImageUrls() != null) existing.setImageUrls(updates.getImageUrls());

        return houseRepo.save(existing);
    }

    @Override
    public HouseModel deactivateHouse(String actorId, String houseId) {
        HouseModel existing = getById(houseId);
        ensureAdminOrOwner(actorId, existing);
        existing.setActive(false);
        return houseRepo.save(existing);
    }

    @Override
    public HouseModel activateHouse(String actorId, String houseId) {
        HouseModel existing = getById(houseId);
        ensureAdminOrOwner(actorId, existing);
        existing.setActive(true);
        return houseRepo.save(existing);
    }

    @Override
    public HouseModel getById(String houseId) {
        return houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Objekat ne postoji."));
    }

    @Override
    public List<HouseModel> getActiveHouses() {
        return houseRepo.findByIsActiveTrue();
    }

    @Override
    public List<HouseModel> getHousesByOwner(String ownerId) {
        return houseRepo.findByOwnerId(ownerId);
    }

    private void ensureAdminOrOwner(String actorId, HouseModel house) {
        UserModel actor = userRepo.findById(actorId)
                .orElseThrow(() -> new ApiException("Korisnik ne postoji."));

        boolean isAdmin = actor.getRole() == UserModel.Role.ADMIN;
        boolean isOwner = house.getOwnerId() != null && house.getOwnerId().equals(actorId);

        if (!isAdmin && !isOwner) {
            throw new ApiException("Nemaš pravo za ovu akciju.");
        }
    }
}
