package com.absolute.cinema.filmorabackend.dto;

import com.absolute.cinema.filmorabackend.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private Long hallId;
    private String hallName;
    private String rowLetter;
    private Integer seatNumber;
    private String seatLabel;
    private SeatType seatType;
    private boolean isReserved;
    private BigDecimal price;
}
