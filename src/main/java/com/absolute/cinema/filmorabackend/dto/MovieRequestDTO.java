package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private Double voteAverage;
    private Integer voteCount;
    private LocalDate releaseDate;
    private Integer runtime;
    private String status;
    private Double popularity;
    private String originalLanguage;
    private String originalTitle;
    private Long tmdbId;
    private Set<Long> genreIds;
}