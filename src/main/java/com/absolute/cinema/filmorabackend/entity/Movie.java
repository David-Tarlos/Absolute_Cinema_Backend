package com.absolute.cinema.filmorabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String overview;

    private String posterPath;

    private String backdropPath;

    private Double voteAverage;

    private Integer voteCount;

    private LocalDate releaseDate;

    private Integer runtime;

    private String status;

    private Double popularity;

    private String originalLanguage;

    private String originalTitle;

    private Boolean adult;

    private String trailerKey;

    @ElementCollection
    @CollectionTable(name = "movie_keywords", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("creditOrder ASC")
    private List<Cast> cast = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "movie_production_companies",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private Set<ProductionCompany> productionCompanies = new HashSet<>();
}