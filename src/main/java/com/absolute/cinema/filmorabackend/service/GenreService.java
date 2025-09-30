package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.GenreDto;
import com.absolute.cinema.filmorabackend.entity.Genre;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.repository.GenreRepository;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public Page<GenreDto> getAllGenres(Pageable pageable) {
        log.info("Fetching all genres with pagination");
        return genreRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenres() {
        log.info("Fetching all genres");
        return genreRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GenreDto getGenreById(Long id) {
        log.info("Fetching genre with ID: {}", id);
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));
        return convertToDto(genre);
    }

    @Transactional
    public GenreDto createGenre(GenreDto genreDto) {
        log.info("Creating new genre: {}", genreDto.getName());

        Genre genre = new Genre();
        genre.setId(genreDto.getId());
        genre.setName(genreDto.getName());

        if (genreDto.getMovieIds() != null && !genreDto.getMovieIds().isEmpty()) {
            Set<Movie> movies = new HashSet<>();
            for (Long movieId : genreDto.getMovieIds()) {
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
                movies.add(movie);
            }
            genre.setMovies(movies);
        }

        Genre savedGenre = genreRepository.save(genre);
        log.info("Genre created with ID: {}", savedGenre.getId());

        return convertToDto(savedGenre);
    }

    @Transactional
    public GenreDto updateGenre(Long id, GenreDto genreDto) {
        log.info("Updating genre with ID: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));

        genre.setName(genreDto.getName());

        if (genreDto.getMovieIds() != null) {
            Set<Movie> movies = new HashSet<>();
            for (Long movieId : genreDto.getMovieIds()) {
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
                movies.add(movie);
            }
            genre.setMovies(movies);
        }

        Genre updatedGenre = genreRepository.save(genre);
        log.info("Genre updated: {}", updatedGenre.getName());

        return convertToDto(updatedGenre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        log.info("Deleting genre with ID: {}", id);

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + id));

        // Remove the genre from all associated movies
        for (Movie movie : genre.getMovies()) {
            movie.getGenres().remove(genre);
        }
        genre.getMovies().clear();

        genreRepository.deleteById(id);
        log.info("Genre deleted with ID: {}", id);
    }

    private GenreDto convertToDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());

        if (genre.getMovies() != null) {
            Set<Long> movieIds = genre.getMovies().stream()
                    .map(Movie::getId)
                    .collect(Collectors.toSet());
            dto.setMovieIds(movieIds);
        }

        return dto;
    }
}