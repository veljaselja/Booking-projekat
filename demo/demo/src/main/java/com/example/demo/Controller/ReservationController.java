package com.example.demo.Controller;

import com.example.demo.Model.ReservationModel;
import com.example.demo.Service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /* =========================
       GUEST: PODNOŠENJE ZAHTEVA
       ========================= */

    // GUEST kreira zahtev za rezervaciju (status = PENDING)
    @PostMapping
    public ReservationModel requestReservation(@RequestParam String guestId,
                                               @RequestBody ReservationModel req) {
        return reservationService.requestReservation(guestId, req);
    }

    // GUEST otkazuje svoju rezervaciju
    @PostMapping("/{reservationId}/cancel")
    public ReservationModel cancelReservation(@RequestParam String guestId,
                                              @PathVariable String reservationId) {
        return reservationService.cancelReservation(guestId, reservationId);
    }

    /* =========================
       VIEWER: DOSTUPNOST SMEŠTAJA
       ========================= */

    // VIEWER/ANY: proveri dostupnost house-a u periodu
    // primer: /api/reservations/availability?houseId=123&from=2026-01-10&to=2026-01-15
    @GetMapping("/availability")
    public boolean checkAvailability(@RequestParam String houseId,
                                     @RequestParam String from,
                                     @RequestParam String to) {
        LocalDate dateFrom = LocalDate.parse(from);
        LocalDate dateTo = LocalDate.parse(to);
        return reservationService.isHouseAvailable(houseId, dateFrom, dateTo);
    }

    /* =========================
       PREGLED REZERVACIJA
       ========================= */

    @GetMapping("/{reservationId}")
    public ReservationModel getById(@PathVariable String reservationId) {
        return reservationService.getById(reservationId);
    }

    // Sve rezervacije jednog gosta
    @GetMapping("/guest/{guestId}")
    public List<ReservationModel> getByGuest(@PathVariable String guestId) {
        return reservationService.getByGuest(guestId);
    }

    // Sve rezervacije za određeni smeštaj
    @GetMapping("/house/{houseId}")
    public List<ReservationModel> getByHouse(@PathVariable String houseId) {
        return reservationService.getByHouse(houseId);
    }
}
