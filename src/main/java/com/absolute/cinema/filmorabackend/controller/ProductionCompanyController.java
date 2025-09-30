package com.absolute.cinema.filmorabackend.controller;

import com.absolute.cinema.filmorabackend.dto.ProductionCompanyDto;
import com.absolute.cinema.filmorabackend.service.ProductionCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductionCompanyController {

    private final ProductionCompanyService productionCompanyService;

    @GetMapping
    public ResponseEntity<Page<ProductionCompanyDto>> getAllProductionCompanies(Pageable pageable) {
        Page<ProductionCompanyDto> companies = productionCompanyService.getAllProductionCompanies(pageable);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionCompanyDto> getProductionCompanyById(@PathVariable Long id) {
        ProductionCompanyDto company = productionCompanyService.getProductionCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @PostMapping
    public ResponseEntity<ProductionCompanyDto> createProductionCompany(@RequestBody ProductionCompanyDto companyDto) {
        ProductionCompanyDto createdCompany = productionCompanyService.createProductionCompany(companyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionCompanyDto> updateProductionCompany(@PathVariable Long id, @RequestBody ProductionCompanyDto companyDto) {
        ProductionCompanyDto updatedCompany = productionCompanyService.updateProductionCompany(id, companyDto);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductionCompany(@PathVariable Long id) {
        productionCompanyService.deleteProductionCompany(id);
        return ResponseEntity.noContent().build();
    }
}