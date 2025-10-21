package com.absolute.cinema.filmorabackend.entity;

import com.absolute.cinema.filmorabackend.enums.SeatType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket_prices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"showtime_id", "seat_type"})
})
@Getter
@Setter
@NoArgsConstructor
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    @JsonIgnore
    private Showtime showtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "ticketPrice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReservationSeat> reservationSeats = new ArrayList<>();
}
