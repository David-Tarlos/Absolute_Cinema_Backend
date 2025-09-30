package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.Cast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
}