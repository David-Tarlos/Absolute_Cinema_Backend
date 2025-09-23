package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.MovieRequestDTO;
import com.absolute.cinema.filmorabackend.dto.MovieResponseDTO;
import com.absolute.cinema.filmorabackend.dto.PageResponseDTO;
import com.absolute.cinema.filmorabackend.entity.Genre;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.repository.GenreRepository;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public PageResponseDTO<MovieResponseDTO> getAllMovies(Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return mapToPageResponse(moviePage);
    }

    public MovieResponseDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + id));
        return mapToResponseDTO(movie);
    }

    public PageResponseDTO<MovieResponseDTO> searchMoviesByTitle(String title, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        return mapToPageResponse(moviePage);
    }

    public PageResponseDTO<MovieResponseDTO> getMoviesByGenre(Long genreId, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findByGenreId(genreId, pageable);
        return mapToPageResponse(moviePage);
    }

    public PageResponseDTO<MovieResponseDTO> getMoviesByYear(int year, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findByReleaseYear(year, pageable);
        return mapToPageResponse(moviePage);
    }

    public PageResponseDTO<MovieResponseDTO> getMoviesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findByReleaseDateBetween(startDate, endDate, pageable);
        return mapToPageResponse(moviePage);
    }

    public MovieResponseDTO createMovie(MovieRequestDTO requestDTO) {
        Movie movie = new Movie();
        mapRequestToEntity(requestDTO, movie);
        Movie savedMovie = movieRepository.save(movie);
        log.info("Created new movie: {} with id: {}", savedMovie.getTitle(), savedMovie.getId());
        return mapToResponseDTO(savedMovie);
    }

    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO requestDTO) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + id));

        mapRequestToEntity(requestDTO, movie);
        Movie updatedMovie = movieRepository.save(movie);
        log.info("Updated movie: {} with id: {}", updatedMovie.getTitle(), updatedMovie.getId());
        return mapToResponseDTO(updatedMovie);
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
        log.info("Deleted movie with id: {}", id);
    }

    private void mapRequestToEntity(MovieRequestDTO requestDTO, Movie movie) {
        movie.setTitle(requestDTO.getTitle());
        movie.setOverview(requestDTO.getOverview());
        movie.setPosterPath(requestDTO.getPosterPath());
        movie.setBackdropPath(requestDTO.getBackdropPath());
        movie.setVoteAverage(requestDTO.getVoteAverage());
        movie.setVoteCount(requestDTO.getVoteCount());
        movie.setReleaseDate(requestDTO.getReleaseDate());
        movie.setRuntime(requestDTO.getRuntime());
        movie.setStatus(requestDTO.getStatus());
        movie.setPopularity(requestDTO.getPopularity());
        movie.setOriginalLanguage(requestDTO.getOriginalLanguage());
        movie.setOriginalTitle(requestDTO.getOriginalTitle());
        movie.setTmdbId(requestDTO.getTmdbId());

        if (requestDTO.getGenreIds() != null && !requestDTO.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>();
            for (Long genreId : requestDTO.getGenreIds()) {
                genreRepository.findById(genreId).ifPresent(genres::add);
            }
            movie.setGenres(genres);
        }
    }

    private MovieResponseDTO mapToResponseDTO(Movie movie) {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(movie.getId());
        dto.setTmdbId(movie.getTmdbId());
        dto.setTitle(movie.getTitle());
        dto.setOverview(movie.getOverview());
        dto.setPosterPath(movie.getPosterPath());
        dto.setBackdropPath(movie.getBackdropPath());
        dto.setVoteAverage(movie.getVoteAverage());
        dto.setVoteCount(movie.getVoteCount());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setRuntime(movie.getRuntime());
        dto.setStatus(movie.getStatus());
        dto.setPopularity(movie.getPopularity());
        dto.setOriginalLanguage(movie.getOriginalLanguage());
        dto.setOriginalTitle(movie.getOriginalTitle());
        dto.setGenres(movie.getGenres());
        return dto;
    }

    private PageResponseDTO<MovieResponseDTO> mapToPageResponse(Page<Movie> moviePage) {
        return PageResponseDTO.<MovieResponseDTO>builder()
                .content(moviePage.getContent().stream()
                        .map(this::mapToResponseDTO)
                        .toList())
                .pageNumber(moviePage.getNumber())
                .pageSize(moviePage.getSize())
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .last(moviePage.isLast())
                .first(moviePage.isFirst())
                .empty(moviePage.isEmpty())
                .numberOfElements(moviePage.getNumberOfElements())
                .build();
    }
}