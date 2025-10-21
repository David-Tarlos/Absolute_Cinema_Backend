package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.SeatDto;
import com.absolute.cinema.filmorabackend.entity.Seat;
import com.absolute.cinema.filmorabackend.entity.Showtime;
import com.absolute.cinema.filmorabackend.entity.TicketPrice;
import com.absolute.cinema.filmorabackend.repository.ReservationSeatRepository;
import com.absolute.cinema.filmorabackend.repository.SeatRepository;
import com.absolute.cinema.filmorabackend.repository.ShowtimeRepository;
import com.absolute.cinema.filmorabackend.repository.TicketPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final TicketPriceRepository ticketPriceRepository;

    @Transactional(readOnly = true)
    public List<SeatDto> getAvailableSeatsForShowtime(Long showtimeId) {
        log.info("Fetching available seats for showtime ID: {}", showtimeId);

        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + showtimeId));

        // Get all seats for this hall
        List<Seat> allSeats = seatRepository.findByHall(showtime.getHall());

        // Get reserved seat IDs for this showtime
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIdsForShowtime(showtimeId);

        // Convert to DTOs with reservation status and pricing
        return allSeats.stream()
                .map(seat -> {
                    SeatDto dto = new SeatDto();
                    dto.setId(seat.getId());
                    dto.setHallId(seat.getHall().getId());
                    dto.setHallName(seat.getHall().getName());
                    dto.setRowLetter(seat.getRowLetter());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatLabel(seat.getSeatLabel());
                    dto.setSeatType(seat.getSeatType());
                    dto.setReserved(reservedSeatIds.contains(seat.getId()));

                    // Get price for this seat type
                    TicketPrice ticketPrice = ticketPriceRepository
                            .findByShowtimeAndSeatType(showtime, seat.getSeatType())
                            .orElse(null);

                    if (ticketPrice != null) {
                        dto.setPrice(ticketPrice.getPrice());
                    } else {
                        dto.setPrice(showtime.getBasePrice());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsByHallId(Long hallId) {
        log.info("Fetching all seats for hall ID: {}", hallId);

        return seatRepository.findAll().stream()
                .filter(seat -> seat.getHall().getId().equals(hallId))
                .map(seat -> {
                    SeatDto dto = new SeatDto();
                    dto.setId(seat.getId());
                    dto.setHallId(seat.getHall().getId());
                    dto.setHallName(seat.getHall().getName());
                    dto.setRowLetter(seat.getRowLetter());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatLabel(seat.getSeatLabel());
                    dto.setSeatType(seat.getSeatType());
                    dto.setReserved(false);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
