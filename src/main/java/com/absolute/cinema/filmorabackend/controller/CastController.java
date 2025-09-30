package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.CastDto;
import com.absolute.cinema.filmorabackend.service.CastService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cast")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CastController {

    private final CastService castService;

    @GetMapping
    public ResponseEntity<Page<CastDto>> getAllCast(Pageable pageable) {
        Page<CastDto> cast = castService.getAllCast(pageable);
        return ResponseEntity.ok(cast);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CastDto> getCastById(@PathVariable Long id) {
        CastDto cast = castService.getCastById(id);
        return ResponseEntity.ok(cast);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<CastDto>> getCastByMovieId(@PathVariable Long movieId) {
        List<CastDto> cast = castService.getCastByMovieId(movieId);
        return ResponseEntity.ok(cast);
    }

    @PostMapping
    public ResponseEntity<CastDto> createCast(@RequestBody CastDto castDto) {
        CastDto createdCast = castService.createCast(castDto);
        return new ResponseEntity<>(createdCast, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CastDto> updateCast(@PathVariable Long id, @RequestBody CastDto castDto) {
        CastDto updatedCast = castService.updateCast(id, castDto);
        return ResponseEntity.ok(updatedCast);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCast(@PathVariable Long id) {
        castService.deleteCast(id);
        return ResponseEntity.noContent().build();
    }
}