package com.example.demo.ServiceImpl;

import com.example.demo.Exception.ApiException;
import com.example.demo.Model.RatingCommentModel;
import com.example.demo.Model.ReservationModel;
import com.example.demo.Model.UserModel;
import com.example.demo.Repository.RatingCommentRepository;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.RatingCommentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class RatingCommentServiceImpl implements RatingCommentService {

    private final RatingCommentRepository ratingRepo;
    private final ReservationRepository reservationRepo;
    private final UserRepository userRepo;

    public RatingCommentServiceImpl(RatingCommentRepository ratingRepo,
                                    ReservationRepository reservationRepo,
                                    UserRepository userRepo) {
        this.ratingRepo = ratingRepo;
        this.reservationRepo = reservationRepo;
        this.userRepo = userRepo;
    }

    @Override
    public RatingCommentModel addComment(String guestId, RatingCommentModel req) {
        UserModel guest = userRepo.findById(guestId)
                .orElseThrow(() -> new ApiException("Guest ne postoji."));

        if (guest.getRole() != UserModel.Role.GUEST) {
            throw new ApiException("Samo GUEST može da ostavi komentar.");
        }
        if (guest.getStatus() != UserModel.Status.APPROVED) {
            throw new ApiException("Korisnik nije odobren.");
        }

        if (req.getHouseId() == null || req.getHouseId().isBlank()) {
            throw new ApiException("houseId je obavezan.");
        }
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new ApiException("Rating mora biti u opsegu 1-5.");
        }
        if (req.getComment() == null || req.getComment().isBlank()) {
            throw new ApiException("Komentar je obavezan.");
        }

        // jedan komentar po guest-u po house-u
        if (ratingRepo.existsByHouseIdAndGuestId(req.getHouseId(), guestId)) {
            throw new ApiException("Već si ostavio komentar za ovaj objekat.");
        }

        // mora da je boravio: postoji APPROVED rezervacija + dateTo < danas
        List<ReservationModel> approvedForHouse =
                reservationRepo.findByHouseIdAndStatus(req.getHouseId(), ReservationModel.Status.APPROVED);

        boolean stayed = approvedForHouse.stream().anyMatch(r ->
                guestId.equals(r.getGuestId())
                        && r.getDateTo() != null
                        && r.getDateTo().isBefore(LocalDate.now())
        );

        if (!stayed) {
            throw new ApiException("Možeš komentarisati samo ako si boravio u objektu (završena rezervacija).");
        }

        req.setGuestId(guestId);
        req.setCreatedAt(Instant.now());

        return ratingRepo.save(req);
    }

    @Override
    public List<RatingCommentModel> getByHouse(String houseId) {
        return ratingRepo.findByHouseId(houseId);
    }

    @Override
    public List<RatingCommentModel> getByGuest(String guestId) {
        return ratingRepo.findByGuestId(guestId);
    }
}
