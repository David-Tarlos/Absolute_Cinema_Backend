package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.MovieRequestDTO;
import com.absolute.cinema.filmorabackend.dto.MovieResponseDTO;
import com.absolute.cinema.filmorabackend.dto.PageResponseDTO;
import com.absolute.cinema.filmorabackend.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<MovieResponseDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponseDTO<MovieResponseDTO> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovieById(@PathVariable Long id) {
        MovieResponseDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<MovieResponseDTO>> searchMovies(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponseDTO<MovieResponseDTO> movies = movieService.searchMoviesByTitle(title, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<PageResponseDTO<MovieResponseDTO>> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "popularity") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponseDTO<MovieResponseDTO> movies = movieService.getMoviesByGenre(genreId, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<PageResponseDTO<MovieResponseDTO>> getMoviesByYear(
            @PathVariable int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponseDTO<MovieResponseDTO> movies = movieService.getMoviesByYear(year, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/date-range")
    public ResponseEntity<PageResponseDTO<MovieResponseDTO>> getMoviesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponseDTO<MovieResponseDTO> movies = movieService.getMoviesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@RequestBody MovieRequestDTO movieRequestDTO) {
        log.info("Creating new movie: {}", movieRequestDTO.getTitle());
        MovieResponseDTO createdMovie = movieService.createMovie(movieRequestDTO);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieRequestDTO movieRequestDTO) {
        log.info("Updating movie with id: {}", id);
        MovieResponseDTO updatedMovie = movieService.updateMovie(id, movieRequestDTO);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("Deleting movie with id: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}