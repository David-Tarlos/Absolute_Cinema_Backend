package com.absolute.cinema.filmorabackend.config;

import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbGenreResponse;
import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbMovieResponse;
import com.absolute.cinema.filmorabackend.entity.Genre;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.repository.GenreRepository;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import com.absolute.cinema.filmorabackend.service.TmdbClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final TmdbClientService tmdbClientService;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data import from TMDB...");

        // Load genres first
        loadGenres();

        // Load movies (5 pages = 100 movies)
        loadMovies();

        log.info("Data import completed successfully!");
    }

    @Transactional
    private void loadGenres() {
        log.info("Loading genres from TMDB...");

        List<TmdbGenreResponse> tmdbGenres = tmdbClientService.getGenres();

        if (tmdbGenres.isEmpty()) {
            log.warn("No genres fetched from TMDB");
            return;
        }

        for (TmdbGenreResponse tmdbGenre : tmdbGenres) {
            if (!genreRepository.existsById(tmdbGenre.getId())) {
                Genre genre = new Genre();
                genre.setId(tmdbGenre.getId());
                genre.setName(tmdbGenre.getName());
                genreRepository.save(genre);
                log.debug("Saved genre: {} - {}", genre.getId(), genre.getName());
            }
        }

        log.info("Successfully loaded {} genres", genreRepository.count());
    }

    @Transactional
    private void loadMovies() {
        log.info("Loading movies from TMDB...");

        int totalMoviesSaved = 0;
        int pagesToFetch = 5; // 5 pages * 20 movies = 100 movies

        for (int page = 1; page <= pagesToFetch; page++) {
            log.info("Fetching page {} of {}", page, pagesToFetch);

            List<TmdbMovieResponse> movies = tmdbClientService.getPopularMovies(page);

            for (TmdbMovieResponse tmdbMovie : movies) {
                try {
                    if (movieRepository.existsByTmdbId(tmdbMovie.getId())) {
                        log.debug("Movie already exists: {} (TMDB ID: {})",
                                tmdbMovie.getTitle(), tmdbMovie.getId());
                        continue;
                    }

                    // Fetch full movie details for runtime and other info
                    TmdbMovieResponse movieDetails = tmdbClientService.getMovieDetails(tmdbMovie.getId());
                    if (movieDetails != null) {
                        tmdbMovie.setRuntime(movieDetails.getRuntime());
                        tmdbMovie.setStatus(movieDetails.getStatus());
                    }

                    Movie movie = convertToMovie(tmdbMovie);
                    movieRepository.save(movie);
                    totalMoviesSaved++;

                    log.info("Saved movie: {} (TMDB ID: {})", movie.getTitle(), movie.getTmdbId());

                } catch (Exception e) {
                    log.error("Error saving movie {}: {}", tmdbMovie.getTitle(), e.getMessage());
                }
            }

            // Add a small delay between pages to avoid rate limiting
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting between API calls");
            }
        }

        log.info("Successfully loaded {} new movies (total in DB: {})",
                totalMoviesSaved, movieRepository.count());
    }

    private Movie convertToMovie(TmdbMovieResponse tmdbMovie) {
        Movie movie = new Movie();
        movie.setTmdbId(tmdbMovie.getId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setBackdropPath(tmdbMovie.getBackdropPath());
        movie.setVoteAverage(tmdbMovie.getVoteAverage());
        movie.setVoteCount(tmdbMovie.getVoteCount());
        movie.setReleaseDate(tmdbMovie.getReleaseDate());
        movie.setRuntime(tmdbMovie.getRuntime());
        movie.setStatus(tmdbMovie.getStatus());
        movie.setPopularity(tmdbMovie.getPopularity());
        movie.setOriginalLanguage(tmdbMovie.getOriginalLanguage());
        movie.setOriginalTitle(tmdbMovie.getOriginalTitle());

        // Map genre IDs to existing Genre entities from database
        if (tmdbMovie.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>();
            for (Long genreId : tmdbMovie.getGenreIds()) {
                // Fetch the genre from database to ensure we use the persisted entity
                genreRepository.findById(genreId).ifPresent(genres::add);
            }
            movie.setGenres(genres);
        }

        return movie;
    }
}