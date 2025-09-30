package com.absolute.cinema.filmorabackend.dto;

import com.absolute.cinema.filmorabackend.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {
    private Long id;
    private Long tmdbId;
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
    private Set<Genre> genres;
}