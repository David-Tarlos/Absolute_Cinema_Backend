package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.CastDto;
import com.absolute.cinema.filmorabackend.entity.Cast;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.repository.CastRepository;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CastService {

    private final CastRepository castRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public Page<CastDto> getAllCast(Pageable pageable) {
        log.info("Fetching all cast members with pagination");
        return castRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<CastDto> getCastByMovieId(Long movieId) {
        log.info("Fetching cast for movie ID: {}", movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));

        return movie.getCast().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CastDto getCastById(Long id) {
        log.info("Fetching cast member with ID: {}", id);
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cast member not found with id: " + id));
        return convertToDto(cast);
    }

    @Transactional
    public CastDto createCast(CastDto castDto) {
        log.info("Creating new cast member: {}", castDto.getName());

        Movie movie = movieRepository.findById(castDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + castDto.getMovieId()));

        Cast cast = new Cast();
        cast.setTmdbId(castDto.getTmdbId());
        cast.setName(castDto.getName());
        cast.setCharacter(castDto.getCharacter());
        cast.setProfilePath(castDto.getProfilePath());
        cast.setCreditOrder(castDto.getCreditOrder());
        cast.setMovie(movie);

        Cast savedCast = castRepository.save(cast);
        log.info("Cast member created with ID: {}", savedCast.getId());

        return convertToDto(savedCast);
    }

    @Transactional
    public CastDto updateCast(Long id, CastDto castDto) {
        log.info("Updating cast member with ID: {}", id);

        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cast member not found with id: " + id));

        cast.setName(castDto.getName());
        cast.setCharacter(castDto.getCharacter());
        cast.setProfilePath(castDto.getProfilePath());
        cast.setCreditOrder(castDto.getCreditOrder());

        if (castDto.getMovieId() != null && !castDto.getMovieId().equals(cast.getMovie().getId())) {
            Movie movie = movieRepository.findById(castDto.getMovieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found with id: " + castDto.getMovieId()));
            cast.setMovie(movie);
        }

        Cast updatedCast = castRepository.save(cast);
        log.info("Cast member updated: {}", updatedCast.getName());

        return convertToDto(updatedCast);
    }

    @Transactional
    public void deleteCast(Long id) {
        log.info("Deleting cast member with ID: {}", id);

        if (!castRepository.existsById(id)) {
            throw new RuntimeException("Cast member not found with id: " + id);
        }

        castRepository.deleteById(id);
        log.info("Cast member deleted with ID: {}", id);
    }

    private CastDto convertToDto(Cast cast) {
        CastDto dto = new CastDto();
        dto.setId(cast.getId());
        dto.setTmdbId(cast.getTmdbId());
        dto.setName(cast.getName());
        dto.setCharacter(cast.getCharacter());
        dto.setProfilePath(cast.getProfilePath());
        dto.setCreditOrder(cast.getCreditOrder());

        if (cast.getMovie() != null) {
            dto.setMovieId(cast.getMovie().getId());
            dto.setMovieTitle(cast.getMovie().getTitle());
        }

        return dto;
    }
}