package com.example.demo.Controller;

import com.example.demo.Model.RatingCommentModel;
import com.example.demo.Service.RatingCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings-comments")
@CrossOrigin(origins = "http://localhost:4200")
public class RatingCommentController {

    private final RatingCommentService ratingCommentService;

    public RatingCommentController(RatingCommentService ratingCommentService) {
        this.ratingCommentService = ratingCommentService;
    }

    /* =========================
       GUEST: DODAVANJE KOMENTARA
       ========================= */

    // GUEST dodaje komentar i ocenu (samo ako je boravio u objektu)
    @PostMapping
    public RatingCommentModel addComment(@RequestParam String guestId,
                                         @RequestBody RatingCommentModel req) {
        return ratingCommentService.addComment(guestId, req);
    }

    /* =========================
       VIEWER/ANY: PREGLED KOMENTARA
       ========================= */

    // Svi komentari za određeni house (viewer može da vidi)
    @GetMapping("/house/{houseId}")
    public List<RatingCommentModel> getByHouse(@PathVariable String houseId) {
        return ratingCommentService.getByHouse(houseId);
    }

    // Svi komentari jednog gosta (opciono, korisno za profil)
    @GetMapping("/guest/{guestId}")
    public List<RatingCommentModel> getByGuest(@PathVariable String guestId) {
        return ratingCommentService.getByGuest(guestId);
    }
}
