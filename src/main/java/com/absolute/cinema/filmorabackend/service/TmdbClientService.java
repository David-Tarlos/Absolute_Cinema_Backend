package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbGenreListResponse;
import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbGenreResponse;
import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbMovieResponse;
import com.absolute.cinema.filmorabackend.dto.tmdb.TmdbPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class TmdbClientService {

    private final WebClient webClient;
    private final String apiKey;

    public TmdbClientService(@Value("${tmdb.api.base.url}") String baseUrl,
                            @Value("${tmdb.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
        log.info("TMDB Client initialized with base URL: {}", baseUrl);
    }

    public List<TmdbMovieResponse> getPopularMovies(int page) {
        log.info("Fetching popular movies from TMDB, page: {}", page);

        try {
            TmdbPageResponse<TmdbMovieResponse> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("page", page)
                            .queryParam("language", "en-US")
                            .queryParam("include_adult", false)
                            .queryParam("region", "US")
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<TmdbPageResponse<TmdbMovieResponse>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response != null && response.getResults() != null) {
                log.info("Successfully fetched {} movies from page {}",
                        response.getResults().size(), page);
                return response.getResults();
            }

            log.warn("No results found for page {}", page);
            return List.of();

        } catch (Exception e) {
            log.error("Error fetching popular movies from TMDB: {}", e.getMessage());
            return List.of();
        }
    }

    public TmdbMovieResponse getMovieDetails(Long movieId) {
        log.info("Fetching movie details for ID: {}", movieId);

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                TmdbMovieResponse response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/movie/{movieId}")
                                .queryParam("api_key", apiKey)
                                .queryParam("language", "en-US")
                                .queryParam("append_to_response", "videos,keywords,credits")
                                .build(movieId))
                        .retrieve()
                        .bodyToMono(TmdbMovieResponse.class)
                        .timeout(Duration.ofSeconds(15))
                        .block();

                if (response != null) {
                    return response;
                }

            } catch (Exception e) {
                log.warn("Attempt {}/{} failed for movie ID {}: {}",
                        attempt, maxRetries, movieId, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("All attempts failed for movie ID {}: {}", movieId, e.getMessage());
                }
            }
        }

        return null;
    }

    public List<TmdbGenreResponse> getGenres() {
        log.info("Fetching genres from TMDB");

        try {
            TmdbGenreListResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/genre/movie/list")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .build())
                    .retrieve()
                    .bodyToMono(TmdbGenreListResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response != null && response.getGenres() != null) {
                log.info("Successfully fetched {} genres", response.getGenres().size());
                return response.getGenres();
            }

            log.warn("No genres found");
            return List.of();

        } catch (Exception e) {
            log.error("Error fetching genres from TMDB: {}", e.getMessage());
            return List.of();
        }
    }
}