package com.absolute.cinema.filmorabackend.dto.tmdb;

import lombok.Data;

import java.util.List;

@Data
public class TmdbGenreListResponse {
    private List<TmdbGenreResponse> genres;
}