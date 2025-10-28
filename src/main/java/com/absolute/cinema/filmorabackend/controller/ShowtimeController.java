package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.CreateShowtimeRequest;
import com.absolute.cinema.filmorabackend.dto.ShowtimeDto;
import com.absolute.cinema.filmorabackend.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<Page<ShowtimeDto>> getAllShowtimes(Pageable pageable) {
        Page<ShowtimeDto> showtimes = showtimeService.getAllShowtimes(pageable);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDto> getShowtimeById(@PathVariable Long id) {
        ShowtimeDto showtime = showtimeService.getShowtimeById(id);
        return ResponseEntity.ok(showtime);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeDto>> getShowtimesByMovieId(@PathVariable Long movieId) {
        List<ShowtimeDto> showtimes = showtimeService.getShowtimesByMovieId(movieId);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/movie/{movieId}/date")
    public ResponseEntity<List<ShowtimeDto>> getShowtimesByMovieIdAndDate(
            @PathVariable Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<ShowtimeDto> showtimes = showtimeService.getShowtimesByMovieIdAndDate(movieId, date);
        return ResponseEntity.ok(showtimes);
    }

    @PostMapping
    public ResponseEntity<ShowtimeDto> createShowtime(@RequestBody CreateShowtimeRequest request) {
        ShowtimeDto createdShowtime = showtimeService.createShowtime(request);
        return new ResponseEntity<>(createdShowtime, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }
}
