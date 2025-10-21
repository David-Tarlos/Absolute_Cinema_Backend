package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.CreateReservationRequest;
import com.absolute.cinema.filmorabackend.dto.ReservationDto;
import com.absolute.cinema.filmorabackend.dto.SeatDto;
import com.absolute.cinema.filmorabackend.entity.*;
import com.absolute.cinema.filmorabackend.enums.ReservationStatus;
import com.absolute.cinema.filmorabackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Transactional(readOnly = true)
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        log.info("Fetching all reservations with pagination");
        return reservationRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        log.info("Fetching reservation with ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        return convertToDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByEmail(String email) {
        log.info("Fetching reservations for email: {}", email);
        return reservationRepository.findByCustomerEmail(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto createReservation(CreateReservationRequest request) {
        log.info("Creating new reservation for customer: {}", request.getCustomerName());

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + request.getShowtimeId()));

        // Check if seats are available
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIdsForShowtime(showtime.getId());

        for (Long seatId : request.getSeatIds()) {
            if (reservedSeatIds.contains(seatId)) {
                Seat seat = seatRepository.findById(seatId).orElse(null);
                String seatLabel = seat != null ? seat.getSeatLabel() : "Unknown";
                throw new RuntimeException("Seat " + seatLabel + " is already reserved for this showtime");
            }
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setShowtime(showtime);
        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerEmail(request.getCustomerEmail());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        // Calculate total price and create reservation seats
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<ReservationSeat> reservationSeats = new ArrayList<>();

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId));

            TicketPrice ticketPrice = ticketPriceRepository.findByShowtimeAndSeatType(showtime, seat.getSeatType())
                    .orElseThrow(() -> new RuntimeException("Ticket price not found for seat type: " + seat.getSeatType()));

            totalPrice = totalPrice.add(ticketPrice.getPrice());

            ReservationSeat reservationSeat = new ReservationSeat();
            reservationSeat.setReservation(reservation);
            reservationSeat.setSeat(seat);
            reservationSeat.setTicketPrice(ticketPrice);
            reservationSeats.add(reservationSeat);
        }

        reservation.setTotalPrice(totalPrice);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Save reservation seats
        for (ReservationSeat rs : reservationSeats) {
            rs.setReservation(savedReservation);
            reservationSeatRepository.save(rs);
        }

        log.info("Reservation created with ID: {} for {} seats", savedReservation.getId(), request.getSeatIds().size());

        return convertToDto(savedReservation);
    }

    @Transactional
    public ReservationDto cancelReservation(Long id) {
        log.info("Canceling reservation with ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);

        log.info("Reservation cancelled: {}", id);

        return convertToDto(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        log.info("Deleting reservation with ID: {}", id);

        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        reservationRepository.deleteById(id);
        log.info("Reservation deleted with ID: {}", id);
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setShowtimeId(reservation.getShowtime().getId());
        dto.setMovieTitle(reservation.getShowtime().getMovie().getTitle());
        dto.setHallName(reservation.getShowtime().getHall().getName());
        dto.setShowtimeStart(reservation.getShowtime().getStartTime());
        dto.setCustomerName(reservation.getCustomerName());
        dto.setCustomerEmail(reservation.getCustomerEmail());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());

        // Get seats
        List<SeatDto> seats = reservationSeatRepository.findByReservation(reservation).stream()
                .map(rs -> {
                    SeatDto seatDto = new SeatDto();
                    seatDto.setId(rs.getSeat().getId());
                    seatDto.setSeatLabel(rs.getSeat().getSeatLabel());
                    seatDto.setRowLetter(rs.getSeat().getRowLetter());
                    seatDto.setSeatNumber(rs.getSeat().getSeatNumber());
                    seatDto.setSeatType(rs.getSeat().getSeatType());
                    seatDto.setPrice(rs.getTicketPrice().getPrice());
                    return seatDto;
                })
                .collect(Collectors.toList());
        dto.setSeats(seats);

        return dto;
    }
}
