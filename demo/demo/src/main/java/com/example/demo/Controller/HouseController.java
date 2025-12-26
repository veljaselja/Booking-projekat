package com.example.demo.Controller;

import com.example.demo.Model.HouseModel;
import com.example.demo.Model.ReservationModel;
import com.example.demo.Service.HouseService;
import com.example.demo.Service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@CrossOrigin(origins = "http://localhost:4200")
public class HouseController {

    private final HouseService houseService;
    private final ReservationService reservationService;

    public HouseController(HouseService houseService,
                           ReservationService reservationService) {
        this.houseService = houseService;
        this.reservationService = reservationService;
    }

    /* =========================
       VIEWER
       ========================= */

    // VIEWER: lista svih aktivnih kuća
    @GetMapping
    public List<HouseModel> getActiveHouses() {
        return houseService.getActiveHouses();
    }

    // VIEWER: detalji kuće
    @GetMapping("/{houseId}")
    public HouseModel getHouseById(@PathVariable String houseId) {
        return houseService.getById(houseId);
    }

    /* =========================
       HOST / ADMIN ENDPOINTI
       ========================= */

    // HOST / ADMIN: kreiranje kuće
    @PostMapping
    public HouseModel createHouse(@RequestParam String ownerId,
                                  @RequestBody HouseModel house) {
        return houseService.createHouse(ownerId, house);
    }

    // HOST / ADMIN: update kuće
    @PutMapping("/{houseId}")
    public HouseModel updateHouse(@RequestParam String actorId,
                                  @PathVariable String houseId,
                                  @RequestBody HouseModel updates) {
        return houseService.updateHouse(actorId, houseId, updates);
    }

    // HOST / ADMIN: deaktivacija kuće
    @PostMapping("/{houseId}/deactivate")
    public HouseModel deactivateHouse(@RequestParam String actorId,
                                      @PathVariable String houseId) {
        return houseService.deactivateHouse(actorId, houseId);
    }

    // HOST / ADMIN: aktivacija kuće
    @PostMapping("/{houseId}/activate")
    public HouseModel activateHouse(@RequestParam String actorId,
                                    @PathVariable String houseId) {
        return houseService.activateHouse(actorId, houseId);
    }

    // HOST: sve kuće vlasnika
    @GetMapping("/owner/{ownerId}")
    public List<HouseModel> getHousesByOwner(@PathVariable String ownerId) {
        return houseService.getHousesByOwner(ownerId);
    }

    /* =========================
       REZERVACIJE ZA KUĆU
       (HOST / ADMIN)
       ========================= */

    // HOST / ADMIN: odobri rezervaciju za ovu kuću
    @PostMapping("/{houseId}/reservations/{reservationId}/approve")
    public ReservationModel approveReservationForHouse(
            @RequestParam String actorId,
            @PathVariable String houseId,
            @PathVariable String reservationId,
            @RequestParam(required = false) String note
    ) {
        // Provera vlasništva i prava je u ReservationService
        return reservationService.approveReservation(actorId, reservationId, note);
    }

    // HOST / ADMIN: odbij rezervaciju za ovu kuću
    @PostMapping("/{houseId}/reservations/{reservationId}/reject")
    public ReservationModel rejectReservationForHouse(
            @RequestParam String actorId,
            @PathVariable String houseId,
            @PathVariable String reservationId,
            @RequestParam(required = false) String note
    ) {
        return reservationService.rejectReservation(actorId, reservationId, note);
    }
}
