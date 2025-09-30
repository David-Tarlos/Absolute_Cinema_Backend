package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastDto {
    private Long id;
    private Long tmdbId;
    private String name;
    private String character;
    private String profilePath;
    private Integer creditOrder;
    private Long movieId;
    private String movieTitle;
}