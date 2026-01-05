package com.example.demo.ServiceImpl;

import com.example.demo.Exception.ApiException;
import com.example.demo.Model.HouseModel;
import com.example.demo.Model.ReservationModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.HouseRepository;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.ReservationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepo;
    private final HouseRepository houseRepo;
    private final UserRepository userRepo;

    public ReservationServiceImpl(ReservationRepository reservationRepo,
                                  HouseRepository houseRepo,
                                  UserRepository userRepo) {
        this.reservationRepo = reservationRepo;
        this.houseRepo = houseRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ReservationModel requestReservation(String guestId, ReservationModel req) {
        UserModel guest = userRepo.findById(guestId)
                .orElseThrow(() -> new ApiException("Guest ne postoji."));

        if (guest.getRole() != UserModel.Role.GUEST) {
            throw new ApiException("Samo GUEST može da kreira rezervaciju.");
        }
        if (guest.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Korisnik nije odobren.");
        }

        if (req.getHouseId() == null || req.getHouseId().isBlank()) {
            throw new ApiException("houseId je obavezan.");
        }

        HouseModel house = houseRepo.findById(req.getHouseId())
                .orElseThrow(() -> new ApiException("Objekat ne postoji."));

        // ✅ umesto isActive()
        if (house.getStatus() != HouseModel.Status.APPROVED) {
            throw new ApiException("Objekat nije odobren (APPROVED).");
        }
        if (house.getStatus() == HouseModel.Status.DISABLED) {
            throw new ApiException("Objekat je onemogućen (DISABLED).");
        }

        LocalDate from = req.getDateFrom();
        LocalDate to = req.getDateTo();

        validateDates(from, to);

        if (req.getGuestsCount() <= 0) throw new ApiException("guestsCount mora biti > 0.");
        if (req.getGuestsCount() > house.getMaxGuests()) {
            throw new ApiException("Broj gostiju prelazi maxGuests za ovaj objekat.");
        }

        // dostupnost - proveravamo samo APPROVED rezervacije
        if (!isHouseAvailable(req.getHouseId(), from, to)) {
            throw new ApiException("Objekat nije dostupan u traženom periodu.");
        }

        req.setGuestId(guestId);
        req.setStatus(ReservationModel.Status.PENDING);
        req.setRequestedAt(Instant.now());

        // reset decision fields
        req.setDecidedAt(null);
        req.setDecidedByUserId(null);
        req.setDecisionNote(null);

        return reservationRepo.save(req);
    }

    @Override
    public ReservationModel approveReservation(String actorId, String reservationId, String note) {
        ReservationModel r = getById(reservationId);
        if (r.getStatus() != ReservationModel.Status.PENDING) {
            throw new ApiException("Samo PENDING rezervacija može biti odobrena.");
        }

        UserModel actor = userRepo.findById(actorId)
                .orElseThrow(() -> new ApiException("Korisnik ne postoji."));

        HouseModel house = houseRepo.findById(r.getHouseId())
                .orElseThrow(() -> new ApiException("Objekat ne postoji."));

        ensureAdminOrOwnerHost(actor, house);

        // re-check availability (bitno zbog konkurentnih zahteva)
        if (!isHouseAvailable(r.getHouseId(), r.getDateFrom(), r.getDateTo())) {
            throw new ApiException("Objekat više nije dostupan u tom periodu.");
        }

        r.setStatus(ReservationModel.Status.APPROVED);
        r.setDecidedAt(Instant.now());
        r.setDecidedByUserId(actorId);
        r.setDecisionNote(note);

        return reservationRepo.save(r);
    }

    @Override
    public ReservationModel rejectReservation(String actorId, String reservationId, String note) {
        ReservationModel r = getById(reservationId);
        if (r.getStatus() != ReservationModel.Status.PENDING) {
            throw new ApiException("Samo PENDING rezervacija može biti odbijena.");
        }

        UserModel actor = userRepo.findById(actorId)
                .orElseThrow(() -> new ApiException("Korisnik ne postoji."));

        HouseModel house = houseRepo.findById(r.getHouseId())
                .orElseThrow(() -> new ApiException("Objekat ne postoji."));

        ensureAdminOrOwnerHost(actor, house);

        r.setStatus(ReservationModel.Status.REJECTED);
        r.setDecidedAt(Instant.now());
        r.setDecidedByUserId(actorId);
        r.setDecisionNote(note);

        return reservationRepo.save(r);
    }

    @Override
    public ReservationModel cancelReservation(String guestId, String reservationId) {
        ReservationModel r = getById(reservationId);

        if (!r.getGuestId().equals(guestId)) {
            throw new ApiException("Nemaš pravo da otkažeš ovu rezervaciju.");
        }

        // predlog pravila: može da otkaže dok nije počelo
        if (!LocalDate.now().isBefore(r.getDateFrom())) {
            throw new ApiException("Ne možeš otkazati na dan početka ili nakon početka rezervacije.");
        }

        if (r.getStatus() == ReservationModel.Status.CANCELLED) {
            return r; // već otkazano
        }

        r.setStatus(ReservationModel.Status.CANCELLED);
        r.setDecidedAt(Instant.now());
        r.setDecidedByUserId(guestId);
        r.setDecisionNote("Cancelled by guest");

        return reservationRepo.save(r);
    }

    @Override
    public boolean isHouseAvailable(String houseId, LocalDate from, LocalDate to) {
        // overlap: existing.dateFrom <= to AND existing.dateTo >= from
        List<ReservationModel> overlaps =
                reservationRepo.findByHouseIdAndStatusAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                        houseId,
                        ReservationModel.Status.APPROVED,
                        to,
                        from
                );
        return overlaps.isEmpty();
    }

    @Override
    public List<ReservationModel> getByGuest(String guestId) {
        return reservationRepo.findByGuestId(guestId);
    }

    @Override
    public List<ReservationModel> getByHouse(String houseId) {
        return reservationRepo.findByHouseId(houseId);
    }

    @Override
    public ReservationModel getById(String reservationId) {
        return reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ApiException("Rezervacija ne postoji."));
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null) throw new ApiException("Datumi su obavezni.");
        if (!from.isBefore(to)) throw new ApiException("dateFrom mora biti pre dateTo.");
        if (from.isBefore(LocalDate.now())) throw new ApiException("dateFrom ne može biti u prošlosti.");
    }

    private void ensureAdminOrOwnerHost(UserModel actor, HouseModel house) {
        if (actor.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Korisnik nije odobren.");
        }

        boolean isAdmin = actor.getRole() == UserModel.Role.ADMIN;

        // ✅ umesto getOwnerId() koristimo hostId
        boolean isOwnerHost = actor.getRole() == UserModel.Role.HOST
                && house.getHostId() != null
                && house.getHostId().equals(actor.getId());

        if (!isAdmin && !isOwnerHost) {
            throw new ApiException("Nemaš pravo da odlučuješ o ovoj rezervaciji.");
        }

        // opcionalno: ako je objekat disabled, niko ne bi trebalo da odobrava
        if (house.getStatus() == HouseModel.Status.DISABLED) {
            throw new ApiException("Objekat je onemogućen (DISABLED).");
        }
    }
}
