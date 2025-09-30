package com.absolute.cinema.filmorabackend.repository;

import com.absolute.cinema.filmorabackend.entity.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionCompanyRepository extends JpaRepository<ProductionCompany, Long> {
    boolean existsById(Long id);
}