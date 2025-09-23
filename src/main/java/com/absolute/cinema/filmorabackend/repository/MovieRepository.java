package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Long tmdbId);
    boolean existsByTmdbId(Long tmdbId);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    Page<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year")
    Page<Movie> findByReleaseYear(@Param("year") int year, Pageable pageable);
}