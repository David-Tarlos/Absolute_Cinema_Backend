package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHallRequest {
    private String name;
    private Integer totalRows;
    private Integer totalColumns;
    private List<String> vipRows; // e.g., ["E", "F", "G"] for VIP rows
}
