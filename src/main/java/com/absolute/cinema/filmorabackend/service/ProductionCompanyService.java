package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.ProductionCompanyDto;
import com.absolute.cinema.filmorabackend.entity.Movie;
import com.absolute.cinema.filmorabackend.entity.ProductionCompany;
import com.absolute.cinema.filmorabackend.repository.MovieRepository;
import com.absolute.cinema.filmorabackend.repository.ProductionCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionCompanyService {

    private final ProductionCompanyRepository productionCompanyRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public Page<ProductionCompanyDto> getAllProductionCompanies(Pageable pageable) {
        log.info("Fetching all production companies with pagination");
        return productionCompanyRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public ProductionCompanyDto getProductionCompanyById(Long id) {
        log.info("Fetching production company with ID: {}", id);
        ProductionCompany company = productionCompanyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production company not found with id: " + id));
        return convertToDto(company);
    }

    @Transactional
    public ProductionCompanyDto createProductionCompany(ProductionCompanyDto companyDto) {
        log.info("Creating new production company: {}", companyDto.getName());

        ProductionCompany company = new ProductionCompany();
        company.setId(companyDto.getId());
        company.setName(companyDto.getName());
        company.setLogoPath(companyDto.getLogoPath());
        company.setOriginCountry(companyDto.getOriginCountry());

        if (companyDto.getMovieIds() != null && !companyDto.getMovieIds().isEmpty()) {
            Set<Movie> movies = new HashSet<>();
            for (Long movieId : companyDto.getMovieIds()) {
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
                movies.add(movie);
            }
            company.setMovies(movies);
        }

        ProductionCompany savedCompany = productionCompanyRepository.save(company);
        log.info("Production company created with ID: {}", savedCompany.getId());

        return convertToDto(savedCompany);
    }

    @Transactional
    public ProductionCompanyDto updateProductionCompany(Long id, ProductionCompanyDto companyDto) {
        log.info("Updating production company with ID: {}", id);

        ProductionCompany company = productionCompanyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production company not found with id: " + id));

        company.setName(companyDto.getName());
        company.setLogoPath(companyDto.getLogoPath());
        company.setOriginCountry(companyDto.getOriginCountry());

        if (companyDto.getMovieIds() != null) {
            Set<Movie> movies = new HashSet<>();
            for (Long movieId : companyDto.getMovieIds()) {
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
                movies.add(movie);
            }
            company.setMovies(movies);
        }

        ProductionCompany updatedCompany = productionCompanyRepository.save(company);
        log.info("Production company updated: {}", updatedCompany.getName());

        return convertToDto(updatedCompany);
    }

    @Transactional
    public void deleteProductionCompany(Long id) {
        log.info("Deleting production company with ID: {}", id);

        ProductionCompany company = productionCompanyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production company not found with id: " + id));

        // Remove the company from all associated movies
        for (Movie movie : company.getMovies()) {
            movie.getProductionCompanies().remove(company);
        }
        company.getMovies().clear();

        productionCompanyRepository.deleteById(id);
        log.info("Production company deleted with ID: {}", id);
    }

    private ProductionCompanyDto convertToDto(ProductionCompany company) {
        ProductionCompanyDto dto = new ProductionCompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setLogoPath(company.getLogoPath());
        dto.setOriginCountry(company.getOriginCountry());

        if (company.getMovies() != null) {
            Set<Long> movieIds = company.getMovies().stream()
                    .map(Movie::getId)
                    .collect(Collectors.toSet());
            dto.setMovieIds(movieIds);
        }

        return dto;
    }
}