package com.example.demo.ServiceImpl;

import com.example.demo.Exception.ApiException;
import com.example.demo.Model.HouseModel;
import com.example.demo.Model.ReservationModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.HouseRepository;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Service.HouseService;
import com.example.demo.Service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepo;
    private final UserService userService;
    private final ReservationRepository reservationRepo;

    public HouseServiceImpl(
            HouseRepository houseRepo,
            UserService userService,
            ReservationRepository reservationRepo
    ) {
        this.houseRepo = houseRepo;
        this.userService = userService;
        this.reservationRepo = reservationRepo;
    }

    // =========================
    // CREATE
    // =========================
    @Override
    public HouseModel createHouse(String hostId, HouseModel house) {
        UserModel host = userService.getById(hostId);

        if (host.getRole() != UserModel.Role.HOST) {
            throw new ApiException("Samo HOST može da dodaje smeštaj.");
        }
        if (host.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("HOST nije odobren.");
        }

        if (house.getTitle() == null || house.getTitle().isBlank())
            throw new ApiException("Title je obavezan.");
        if (house.getCity() == null || house.getCity().isBlank())
            throw new ApiException("City je obavezan.");
        if (house.getMaxGuests() <= 0)
            throw new ApiException("maxGuests mora biti > 0.");
        if (house.getPricePerNight() == null)
            throw new ApiException("pricePerNight je obavezan.");

        house.setHostId(hostId);
        house.setStatus(HouseModel.Status.PENDING);
        house.setCreatedAt(Instant.now());
        house.setApprovedAt(null);
        house.setApprovedByAdminId(null);

        return houseRepo.save(house);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public HouseModel updateHouse(String hostId, String houseId, HouseModel patch) {
        HouseModel existing = houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Smeštaj ne postoji."));

        if (!existing.getHostId().equals(hostId)) {
            throw new ApiException("Nemaš pravo da menjaš tuđi smeštaj.");
        }
        if (existing.getStatus() == HouseModel.Status.DISABLED) {
            throw new ApiException("Smeštaj je disabled.");
        }

        if (patch.getTitle() != null) existing.setTitle(patch.getTitle());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getCity() != null) existing.setCity(patch.getCity());
        if (patch.getAddress() != null) existing.setAddress(patch.getAddress());
        if (patch.getMaxGuests() > 0) existing.setMaxGuests(patch.getMaxGuests());
        if (patch.getPricePerNight() != null) existing.setPricePerNight(patch.getPricePerNight());
        if (patch.getImageUrls() != null) existing.setImageUrls(patch.getImageUrls());

        // nakon izmene ponovo ide na PENDING
        existing.setStatus(HouseModel.Status.PENDING);
        existing.setApprovedAt(null);
        existing.setApprovedByAdminId(null);

        return houseRepo.save(existing);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteHouse(String hostId, String houseId) {
        HouseModel existing = houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Smeštaj ne postoji."));

        if (!existing.getHostId().equals(hostId)) {
            throw new ApiException("Nemaš pravo da brišeš tuđi smeštaj.");
        }

        houseRepo.deleteById(houseId);
    }

    // =========================
    // HOST
    // =========================
    @Override
    public List<HouseModel> getMyHouses(String hostId) {
        return houseRepo.findByHostId(hostId);
    }

    // =========================
    // ADMIN
    // =========================
    @Override
    public List<HouseModel> getPendingHouses() {
        return houseRepo.findByStatus(HouseModel.Status.PENDING);
    }

    @Override
    public HouseModel approveHouse(String adminId, String houseId) {
        UserModel admin = userService.getById(adminId);
        if (admin.getRole() != UserModel.Role.ADMIN) {
            throw new ApiException("Samo ADMIN može da odobri smeštaj.");
        }

        HouseModel house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Smeštaj ne postoji."));

        if (house.getStatus() != HouseModel.Status.PENDING) {
            throw new ApiException("Smeštaj nije u PENDING statusu.");
        }

        house.setStatus(HouseModel.Status.APPROVED);
        house.setApprovedAt(Instant.now());
        house.setApprovedByAdminId(adminId);

        return houseRepo.save(house);
    }

    @Override
    public HouseModel rejectHouse(String adminId, String houseId) {
        UserModel admin = userService.getById(adminId);
        if (admin.getRole() != UserModel.Role.ADMIN) {
            throw new ApiException("Samo ADMIN može da odbije smeštaj.");
        }

        HouseModel house = houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Smeštaj ne postoji."));

        if (house.getStatus() != HouseModel.Status.PENDING) {
            throw new ApiException("Smeštaj nije u PENDING statusu.");
        }

        house.setStatus(HouseModel.Status.REJECTED);
        house.setApprovedAt(Instant.now());
        house.setApprovedByAdminId(adminId);

        return houseRepo.save(house);
    }

    // =========================
    // BROWSE (BASIC)
    // =========================
    @Override
    public List<HouseModel> browseApproved(String city) {
        if (city == null || city.isBlank()) {
            return houseRepo.findByStatus(HouseModel.Status.APPROVED);
        }
        return houseRepo.findByStatusAndCityIgnoreCase(HouseModel.Status.APPROVED, city);
    }

    // =========================
    // BROWSE (ADVANCED – DATES + GUESTS)
    // =========================
    @Override
    public List<HouseModel> searchAvailable(String city, Integer guests, LocalDate from, LocalDate to) {

        List<HouseModel> base = browseApproved(city);

        if (from != null && to != null) {
            if (!from.isBefore(to)) {
                throw new ApiException("dateFrom mora biti pre dateTo.");
            }
            if (from.isBefore(LocalDate.now())) {
                throw new ApiException("dateFrom ne može biti u prošlosti.");
            }
        }

        List<HouseModel> result = new ArrayList<>();

        for (HouseModel house : base) {

            if (guests != null && guests > 0 && house.getMaxGuests() < guests) {
                continue;
            }

            if (from != null && to != null) {
                if (!isHouseAvailable(house.getId(), from, to)) {
                    continue;
                }
            }

            result.add(house);
        }

        return result;
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public HouseModel getApprovedById(String houseId) {
        HouseModel h = houseRepo.findById(houseId)
                .orElseThrow(() -> new ApiException("Smeštaj ne postoji."));
        if (h.getStatus() != HouseModel.Status.APPROVED) {
            throw new ApiException("Smeštaj nije odobren.");
        }
        return h;
    }

    // =========================
    // AVAILABILITY CHECK
    // =========================
    private boolean isHouseAvailable(String houseId, LocalDate from, LocalDate to) {
        List<ReservationModel> overlaps =
                reservationRepo.findByHouseIdAndStatusAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                        houseId,
                        ReservationModel.Status.APPROVED,
                        to,
                        from
                );
        return overlaps == null || overlaps.isEmpty();
    }
}
