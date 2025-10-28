package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Showtime;
import com.absolute.cinema.filmorabackend.entity.TicketPrice;
import com.absolute.cinema.filmorabackend.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long> {
    List<TicketPrice> findByShowtime(Showtime showtime);
    Optional<TicketPrice> findByShowtimeAndSeatType(Showtime showtime, SeatType seatType);
}
