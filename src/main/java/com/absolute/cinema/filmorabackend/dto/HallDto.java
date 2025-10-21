package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HallDto {
    private Long id;
    private String name;
    private Integer totalRows;
    private Integer totalColumns;
    private Integer totalSeats;
}
