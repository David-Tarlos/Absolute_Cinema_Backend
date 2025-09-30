package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompanyDto {
    private Long id;
    private String name;
    private String logoPath;
    private String originCountry;
    private Set<Long> movieIds;
}