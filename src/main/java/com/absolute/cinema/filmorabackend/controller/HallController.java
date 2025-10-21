package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.CreateHallRequest;
import com.absolute.cinema.filmorabackend.dto.HallDto;
import com.absolute.cinema.filmorabackend.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HallController {

    private final HallService hallService;

    @GetMapping
    public ResponseEntity<?> getAllHalls(
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            Pageable pageable) {
        if (paginated) {
            Page<HallDto> halls = hallService.getAllHalls(pageable);
            return ResponseEntity.ok(halls);
        } else {
            List<HallDto> halls = hallService.getAllHalls();
            return ResponseEntity.ok(halls);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallDto> getHallById(@PathVariable Long id) {
        HallDto hall = hallService.getHallById(id);
        return ResponseEntity.ok(hall);
    }

    @PostMapping
    public ResponseEntity<HallDto> createHall(@RequestBody CreateHallRequest request) {
        HallDto createdHall = hallService.createHall(request);
        return new ResponseEntity<>(createdHall, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HallDto> updateHall(@PathVariable Long id, @RequestBody HallDto hallDto) {
        HallDto updatedHall = hallService.updateHall(id, hallDto);
        return ResponseEntity.ok(updatedHall);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}
