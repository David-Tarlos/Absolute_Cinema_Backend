package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Hall;
import com.absolute.cinema.filmorabackend.entity.Seat;
import com.absolute.cinema.filmorabackend.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByHall(Hall hall);
    List<Seat> findByHallAndSeatType(Hall hall, SeatType seatType);
    Optional<Seat> findByHallAndRowLetterAndSeatNumber(Hall hall, String rowLetter, Integer seatNumber);
}
