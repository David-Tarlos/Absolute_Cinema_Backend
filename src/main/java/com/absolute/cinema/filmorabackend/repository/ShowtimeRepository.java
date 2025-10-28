package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Hall;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovie(Movie movie);
    List<Showtime> findByHall(Hall hall);
    List<Showtime> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :date ORDER BY s.startTime")
    List<Showtime> findByMovieIdAndDate(@Param("movieId") Long movieId, @Param("date") LocalDateTime date);

    @Query("SELECT s FROM Showtime s WHERE s.hall.id = :hallId AND " +
           "((s.startTime BETWEEN :start AND :end) OR (s.endTime BETWEEN :start AND :end) OR " +
           "(s.startTime <= :start AND s.endTime >= :end))")
    List<Showtime> findConflictingShowtimes(@Param("hallId") Long hallId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
