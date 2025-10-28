package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.SeatDto;
import com.absolute.cinema.filmorabackend.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/showtime/{showtimeId}/availability")
    public ResponseEntity<List<SeatDto>> getAvailableSeatsForShowtime(@PathVariable Long showtimeId) {
        List<SeatDto> seats = seatService.getAvailableSeatsForShowtime(showtimeId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<SeatDto>> getSeatsByHallId(@PathVariable Long hallId) {
        List<SeatDto> seats = seatService.getSeatsByHallId(hallId);
        return ResponseEntity.ok(seats);
    }
}
