package com.absolute.cinema.filmorabackend.dto.tmdb;

import lombok.Data;
import java.util.List;

@Data
public class TmdbCreditsResponse {
    private List<TmdbCastResponse> cast;
    private List<TmdbCrewResponse> crew;
}