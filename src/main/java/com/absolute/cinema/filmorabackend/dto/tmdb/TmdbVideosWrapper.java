package com.absolute.cinema.filmorabackend.dto.tmdb;

import lombok.Data;
import java.util.List;

@Data
public class TmdbVideosWrapper {
    private List<TmdbVideoResponse> results;
}