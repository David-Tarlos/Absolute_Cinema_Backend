package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Reservation;
import com.absolute.cinema.filmorabackend.entity.ReservationSeat;
import com.absolute.cinema.filmorabackend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findByReservation(Reservation reservation);
    List<ReservationSeat> findBySeat(Seat seat);

    @Query("SELECT rs FROM ReservationSeat rs " +
           "WHERE rs.reservation.showtime.id = :showtimeId " +
           "AND rs.reservation.status IN ('PENDING', 'CONFIRMED')")
    List<ReservationSeat> findReservedSeatsForShowtime(@Param("showtimeId") Long showtimeId);

    @Query("SELECT rs.seat.id FROM ReservationSeat rs " +
           "WHERE rs.reservation.showtime.id = :showtimeId " +
           "AND rs.reservation.status IN ('PENDING', 'CONFIRMED')")
    List<Long> findReservedSeatIdsForShowtime(@Param("showtimeId") Long showtimeId);
}
