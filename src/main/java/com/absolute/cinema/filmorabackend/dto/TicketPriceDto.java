package com.absolute.cinema.filmorabackend.dto;

import com.absolute.cinema.filmorabackend.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPriceDto {
    private Long id;
    private Long showtimeId;
    private SeatType seatType;
    private BigDecimal price;
}
