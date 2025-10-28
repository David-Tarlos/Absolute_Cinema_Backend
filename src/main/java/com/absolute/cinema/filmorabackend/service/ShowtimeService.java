package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.CreateShowtimeRequest;
import com.absolute.cinema.filmorabackend.dto.ShowtimeDto;
import com.absolute.cinema.filmorabackend.dto.TicketPriceDto;
import com.absolute.cinema.filmorabackend.entity.*;
import com.absolute.cinema.filmorabackend.enums.SeatType;
import com.absolute.cinema.filmorabackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final TicketPriceRepository ticketPriceRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public Page<ShowtimeDto> getAllShowtimes(Pageable pageable) {
        log.info("Fetching all showtimes with pagination");
        return showtimeRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDto> getShowtimesByMovieId(Long movieId) {
        log.info("Fetching showtimes for movie ID: {}", movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        return showtimeRepository.findByMovie(movie).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDto> getShowtimesByMovieIdAndDate(Long movieId, LocalDateTime date) {
        log.info("Fetching showtimes for movie ID: {} on date: {}", movieId, date);
        return showtimeRepository.findByMovieIdAndDate(movieId, date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowtimeDto getShowtimeById(Long id) {
        log.info("Fetching showtime with ID: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found with id: " + id));
        return convertToDto(showtime);
    }

    @Transactional
    public ShowtimeDto createShowtime(CreateShowtimeRequest request) {
        log.info("Creating new showtime");

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.getMovieId()));

        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new RuntimeException("Hall not found with id: " + request.getHallId()));

        // Check for scheduling conflicts
        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(
                hall.getId(), request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Hall is already booked for this time slot");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setHall(hall);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());
        showtime.setBasePrice(request.getBasePrice());

        Showtime savedShowtime = showtimeRepository.save(showtime);

        // Create ticket prices for different seat types
        if (request.getRegularPrice() != null) {
            TicketPrice regularPrice = new TicketPrice();
            regularPrice.setShowtime(savedShowtime);
            regularPrice.setSeatType(SeatType.REGULAR);
            regularPrice.setPrice(request.getRegularPrice());
            ticketPriceRepository.save(regularPrice);
        }

        if (request.getVipPrice() != null) {
            TicketPrice vipPrice = new TicketPrice();
            vipPrice.setShowtime(savedShowtime);
            vipPrice.setSeatType(SeatType.VIP);
            vipPrice.setPrice(request.getVipPrice());
            ticketPriceRepository.save(vipPrice);
        }

        log.info("Showtime created with ID: {}", savedShowtime.getId());

        return convertToDto(savedShowtime);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        log.info("Deleting showtime with ID: {}", id);

        if (!showtimeRepository.existsById(id)) {
            throw new RuntimeException("Showtime not found with id: " + id);
        }

        showtimeRepository.deleteById(id);
        log.info("Showtime deleted with ID: {}", id);
    }

    private ShowtimeDto convertToDto(Showtime showtime) {
        ShowtimeDto dto = new ShowtimeDto();
        dto.setId(showtime.getId());
        dto.setMovieId(showtime.getMovie().getId());
        dto.setMovieTitle(showtime.getMovie().getTitle());
        dto.setMoviePosterPath(showtime.getMovie().getPosterPath());
        dto.setHallId(showtime.getHall().getId());
        dto.setHallName(showtime.getHall().getName());
        dto.setStartTime(showtime.getStartTime());
        dto.setEndTime(showtime.getEndTime());
        dto.setBasePrice(showtime.getBasePrice());

        // Get ticket prices
        List<TicketPriceDto> priceDtos = ticketPriceRepository.findByShowtime(showtime).stream()
                .map(this::convertTicketPriceToDto)
                .collect(Collectors.toList());
        dto.setTicketPrices(priceDtos);

        // Calculate available seats
        int totalSeats = showtime.getHall().getTotalRows() * showtime.getHall().getTotalColumns();
        List<Long> reservedSeatIds = reservationSeatRepository.findReservedSeatIdsForShowtime(showtime.getId());
        dto.setTotalSeats(totalSeats);
        dto.setAvailableSeats(totalSeats - reservedSeatIds.size());

        return dto;
    }

    private TicketPriceDto convertTicketPriceToDto(TicketPrice ticketPrice) {
        TicketPriceDto dto = new TicketPriceDto();
        dto.setId(ticketPrice.getId());
        dto.setShowtimeId(ticketPrice.getShowtime().getId());
        dto.setSeatType(ticketPrice.getSeatType());
        dto.setPrice(ticketPrice.getPrice());
        return dto;
    }
}
