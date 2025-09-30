package com.absolute.cinema.filmorabackend.config;

import com.absolute.cinema.filmorabackend.dto.tmdb.*;
import com.absolute.cinema.filmorabackend.entity.Cast;
import com.absolute.cinema.filmorabackend.entity.Genre;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.entity.ProductionCompany;
import com.absolute.cinema.filmorabackend.repository.CastRepository;
import com.absolute.cinema.filmorabackend.repository.GenreRepository;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import com.absolute.cinema.filmorabackend.repository.ProductionCompanyRepository;
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
    private final ProductionCompanyRepository productionCompanyRepository;
    private final CastRepository castRepository;

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

    private void loadMovies() {
        log.info("Loading movies from TMDB...");

        int targetMovieCount = 100;
        int totalMoviesSaved = 0;
        int currentPage = 1;
        int maxPages = 20; // Safety limit to prevent infinite loops
        int consecutiveFailures = 0;

        while (totalMoviesSaved < targetMovieCount && currentPage <= maxPages) {
            log.info("Fetching page {} (saved so far: {}/{})", currentPage, totalMoviesSaved, targetMovieCount);

            List<TmdbMovieResponse> movies = tmdbClientService.getPopularMovies(currentPage);

            if (movies.isEmpty()) {
                log.warn("No movies returned from page {}. Ending data load.", currentPage);
                break;
            }

            for (TmdbMovieResponse tmdbMovie : movies) {
                if (totalMoviesSaved >= targetMovieCount) {
                    break; // We've reached our target
                }

                try {
                    if (movieRepository.existsByTmdbId(tmdbMovie.getId())) {
                        log.debug("Movie already exists: {} (TMDB ID: {})",
                                tmdbMovie.getTitle(), tmdbMovie.getId());
                        continue;
                    }

                    // Skip adult content
                    if (Boolean.TRUE.equals(tmdbMovie.getAdult())) {
                        log.info("Skipping adult content: {}", tmdbMovie.getTitle());
                        continue;
                    }

                    // Add delay before fetching details to avoid rate limiting
                    try {
                        Thread.sleep(300); // Small delay between each movie detail fetch
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Fetch full movie details with videos, keywords, credits
                    TmdbMovieResponse movieDetails = tmdbClientService.getMovieDetails(tmdbMovie.getId());
                    if (movieDetails != null) {
                        tmdbMovie = movieDetails; // Use the detailed response which has all data
                        consecutiveFailures = 0; // Reset failure counter on success
                    } else {
                        log.warn("Could not fetch detailed info for movie: {} (ID: {}). Skipping.",
                                tmdbMovie.getTitle(), tmdbMovie.getId());
                        consecutiveFailures++;

                        // If we have too many consecutive failures, wait longer
                        if (consecutiveFailures >= 3) {
                            log.warn("Multiple consecutive failures. Waiting 5 seconds before continuing...");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            consecutiveFailures = 0;
                        }
                        continue; // Skip this movie if we can't get full details
                    }

                    // Save the movie in its own transaction
                    saveMovieWithRelations(tmdbMovie);
                    totalMoviesSaved++;

                    log.info("Saved movie {}/{}: {} (TMDB ID: {})",
                            totalMoviesSaved, targetMovieCount, tmdbMovie.getTitle(), tmdbMovie.getId());

                } catch (Exception e) {
                    log.error("Error saving movie {}: {}", tmdbMovie.getTitle(), e.getMessage(), e);
                    consecutiveFailures++;
                }
            }

            currentPage++;

            // Add a longer delay between pages to avoid rate limiting
            if (totalMoviesSaved < targetMovieCount) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Interrupted while waiting between API calls");
                }
            }
        }

        log.info("Data load complete. Successfully loaded {} movies (total in DB: {})",
                totalMoviesSaved, movieRepository.count());

        if (totalMoviesSaved < targetMovieCount) {
            log.warn("Could only load {} out of {} target movies. This may be due to API rate limits or connection issues.",
                    totalMoviesSaved, targetMovieCount);
        }
    }

    @Transactional
    private void saveMovieWithRelations(TmdbMovieResponse tmdbMovie) {
        Movie movie = convertToMovie(tmdbMovie);

        // Handle production companies
        if (tmdbMovie.getProductionCompanies() != null && !tmdbMovie.getProductionCompanies().isEmpty()) {
            Set<ProductionCompany> companies = new HashSet<>();
            for (TmdbProductionCompanyResponse companyResponse : tmdbMovie.getProductionCompanies()) {
                ProductionCompany company = productionCompanyRepository.findById(companyResponse.getId())
                    .orElseGet(() -> {
                        ProductionCompany newCompany = new ProductionCompany();
                        newCompany.setId(companyResponse.getId());
                        newCompany.setName(companyResponse.getName());
                        newCompany.setLogoPath(companyResponse.getLogoPath());
                        newCompany.setOriginCountry(companyResponse.getOriginCountry());
                        return productionCompanyRepository.save(newCompany);
                    });
                companies.add(company);
            }
            movie.setProductionCompanies(companies);
        }

        Movie savedMovie = movieRepository.save(movie);

        // Save cast members after movie is saved
        if (tmdbMovie.getCredits() != null && tmdbMovie.getCredits().getCast() != null) {
            saveCastMembers(savedMovie, tmdbMovie.getCredits().getCast());
        }
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
        movie.setAdult(tmdbMovie.getAdult());

        // Extract trailer key from videos
        if (tmdbMovie.getVideos() != null && tmdbMovie.getVideos().getResults() != null) {
            tmdbMovie.getVideos().getResults().stream()
                    .filter(video -> "YouTube".equals(video.getSite()) &&
                                   ("Trailer".equals(video.getType()) || "Teaser".equals(video.getType())) &&
                                   Boolean.TRUE.equals(video.getOfficial()))
                    .findFirst()
                    .ifPresent(video -> movie.setTrailerKey(video.getKey()));
        }

        // Extract keywords
        if (tmdbMovie.getKeywords() != null && tmdbMovie.getKeywords().getKeywords() != null) {
            Set<String> keywords = tmdbMovie.getKeywords().getKeywords().stream()
                    .map(TmdbKeywordResponse::getName)
                    .collect(Collectors.toSet());
            movie.setKeywords(keywords);
        }

        // Map genres - handle both genre IDs and genre objects
        Set<Genre> genres = new HashSet<>();
        if (tmdbMovie.getGenres() != null) {
            for (TmdbGenreResponse genre : tmdbMovie.getGenres()) {
                genreRepository.findById(genre.getId()).ifPresent(genres::add);
            }
        } else if (tmdbMovie.getGenreIds() != null) {
            for (Long genreId : tmdbMovie.getGenreIds()) {
                genreRepository.findById(genreId).ifPresent(genres::add);
            }
        }
        movie.setGenres(genres);

        return movie;
    }

    private void saveProductionCompanies(List<TmdbProductionCompanyResponse> companies) {
        for (TmdbProductionCompanyResponse company : companies) {
            if (!productionCompanyRepository.existsById(company.getId())) {
                ProductionCompany prodCompany = new ProductionCompany();
                prodCompany.setId(company.getId());
                prodCompany.setName(company.getName());
                prodCompany.setLogoPath(company.getLogoPath());
                prodCompany.setOriginCountry(company.getOriginCountry());
                productionCompanyRepository.save(prodCompany);
                log.debug("Saved production company: {}", prodCompany.getName());
            }
        }
    }

    private void saveCastMembers(Movie movie, List<TmdbCastResponse> castList) {
        // Limit to top 10 cast members
        int limit = Math.min(10, castList.size());
        List<Cast> castMembers = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            TmdbCastResponse tmdbCast = castList.get(i);
            Cast cast = new Cast();
            cast.setTmdbId(tmdbCast.getId());
            cast.setName(tmdbCast.getName());
            cast.setCharacter(tmdbCast.getCharacter());
            cast.setProfilePath(tmdbCast.getProfilePath());
            cast.setCreditOrder(tmdbCast.getOrder());
            cast.setMovie(movie);
            castMembers.add(cast);
            log.debug("Added cast member: {} as {}", cast.getName(), cast.getCharacter());
        }

        castRepository.saveAll(castMembers);
        movie.setCast(castMembers);
    }
}