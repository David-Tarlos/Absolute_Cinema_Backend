package com.absolute.cinema.filmorabackend.dto;

import com.absolute.cinema.filmorabackend.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private Long showtimeId;
    private String movieTitle;
    private String hallName;
    private LocalDateTime showtimeStart;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private List<SeatDto> seats;
}
