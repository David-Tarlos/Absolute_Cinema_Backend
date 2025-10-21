package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Reservation;
import com.absolute.cinema.filmorabackend.entity.Showtime;
import com.absolute.cinema.filmorabackend.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerEmail(String customerEmail);
    List<Reservation> findByShowtime(Showtime showtime);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByShowtimeAndStatus(Showtime showtime, ReservationStatus status);
}
