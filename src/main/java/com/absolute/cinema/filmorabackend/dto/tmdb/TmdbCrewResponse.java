package com.absolute.cinema.filmorabackend.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TmdbCrewResponse {

    private Long id;

    private String name;

    @JsonProperty("original_name")
    private String originalName;

    private String job;

    private String department;

    @JsonProperty("profile_path")
    private String profilePath;

    @JsonProperty("credit_id")
    private String creditId;

    private Boolean adult;

    private Integer gender;

    @JsonProperty("known_for_department")
    private String knownForDepartment;

    private Double popularity;
}