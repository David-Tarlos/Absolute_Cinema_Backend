package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.GenreDto;
import com.absolute.cinema.filmorabackend.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<?> getAllGenres(
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            Pageable pageable) {
        if (paginated) {
            Page<GenreDto> genres = genreService.getAllGenres(pageable);
            return ResponseEntity.ok(genres);
        } else {
            List<GenreDto> genres = genreService.getAllGenres();
            return ResponseEntity.ok(genres);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        GenreDto genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        GenreDto createdGenre = genreService.createGenre(genreDto);
        return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long id, @RequestBody GenreDto genreDto) {
        GenreDto updatedGenre = genreService.updateGenre(id, genreDto);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}