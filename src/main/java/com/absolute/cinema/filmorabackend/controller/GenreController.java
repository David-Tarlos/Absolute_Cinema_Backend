package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.entity.Genre;
import com.absolute.cinema.filmorabackend.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        return genreRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}