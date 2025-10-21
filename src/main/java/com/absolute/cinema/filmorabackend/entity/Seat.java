package com.absolute.cinema.filmorabackend.entity;

import com.absolute.cinema.filmorabackend.enums.SeatType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hall_id", "row_letter", "seat_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    @JsonIgnore
    private Hall hall;

    @Column(name = "row_letter", nullable = false)
    private String rowLetter; // A, B, C, etc.

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    public String getSeatLabel() {
        return rowLetter + seatNumber;
    }
}
