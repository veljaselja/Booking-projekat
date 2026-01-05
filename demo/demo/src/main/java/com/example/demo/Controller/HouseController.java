package com.example.demo.Controller;

import com.example.demo.Model.HouseModel;
import com.example.demo.Service.HouseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@CrossOrigin(origins = "http://localhost:4200")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    // HOST: kreira smeštaj (header X-Host-Id)
    @PostMapping
    public HouseModel create(@RequestHeader("X-Host-Id") String hostId,
                             @RequestBody HouseModel house) {
        return houseService.createHouse(hostId, house);
    }

    // HOST: update smeštaj
    @PutMapping("/{houseId}")
    public HouseModel update(@RequestHeader("X-Host-Id") String hostId,
                             @PathVariable String houseId,
                             @RequestBody HouseModel patch) {
        return houseService.updateHouse(hostId, houseId, patch);
    }

    // HOST: delete smeštaj
    @DeleteMapping("/{houseId}")
    public void delete(@RequestHeader("X-Host-Id") String hostId,
                       @PathVariable String houseId) {
        houseService.deleteHouse(hostId, houseId);
    }

    // HOST: moji smeštaji
    @GetMapping("/mine")
    public List<HouseModel> myHouses(@RequestHeader("X-Host-Id") String hostId) {
        return houseService.getMyHouses(hostId);
    }

    // VIEW/BROWSE: svi odobreni
    @GetMapping
    public List<HouseModel> browse(@RequestParam(required = false) String city) {
        return houseService.browseApproved(city);
    }

    @GetMapping("/search")
    public List<HouseModel> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return houseService.searchAvailable(city, guests, from, to);
    }

    @GetMapping("/{houseId}")
    public HouseModel getApproved(@PathVariable String houseId) {
        return houseService.getApprovedById(houseId);
    }
}
