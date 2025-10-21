package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private Long showtimeId;
    private String customerName;
    private String customerEmail;
    private List<Long> seatIds;
}
