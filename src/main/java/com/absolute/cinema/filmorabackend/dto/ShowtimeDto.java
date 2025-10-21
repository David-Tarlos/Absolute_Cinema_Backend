package com.absolute.cinema.filmorabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterPath;
    private Long hallId;
    private String hallName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private List<TicketPriceDto> ticketPrices;
    private Integer availableSeats;
    private Integer totalSeats;
}
