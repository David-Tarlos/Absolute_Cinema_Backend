package com.absolute.cinema.filmorabackend.service;

import com.absolute.cinema.filmorabackend.dto.CreateHallRequest;
import com.absolute.cinema.filmorabackend.dto.HallDto;
import com.absolute.cinema.filmorabackend.entity.Hall;
import com.absolute.cinema.filmorabackend.entity.Seat;
import com.absolute.cinema.filmorabackend.enums.SeatType;
import com.absolute.cinema.filmorabackend.repository.HallRepository;
import com.absolute.cinema.filmorabackend.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public Page<HallDto> getAllHalls(Pageable pageable) {
        log.info("Fetching all halls with pagination");
        return hallRepository.findAll(pageable).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<HallDto> getAllHalls() {
        log.info("Fetching all halls");
        return hallRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HallDto getHallById(Long id) {
        log.info("Fetching hall with ID: {}", id);
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found with id: " + id));
        return convertToDto(hall);
    }

    @Transactional
    public HallDto createHall(CreateHallRequest request) {
        log.info("Creating new hall: {}", request.getName());

        if (hallRepository.existsByName(request.getName())) {
            throw new RuntimeException("Hall with name '" + request.getName() + "' already exists");
        }

        Hall hall = new Hall();
        hall.setName(request.getName());
        hall.setTotalRows(request.getTotalRows());
        hall.setTotalColumns(request.getTotalColumns());

        Hall savedHall = hallRepository.save(hall);

        // Auto-generate seats
        generateSeatsForHall(savedHall, request.getVipRows());

        log.info("Hall created with ID: {} and {} seats", savedHall.getId(),
                savedHall.getTotalRows() * savedHall.getTotalColumns());

        return convertToDto(savedHall);
    }

    @Transactional
    public HallDto updateHall(Long id, HallDto hallDto) {
        log.info("Updating hall with ID: {}", id);

        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found with id: " + id));

        hall.setName(hallDto.getName());

        Hall updatedHall = hallRepository.save(hall);
        log.info("Hall updated: {}", updatedHall.getName());

        return convertToDto(updatedHall);
    }

    @Transactional
    public void deleteHall(Long id) {
        log.info("Deleting hall with ID: {}", id);

        if (!hallRepository.existsById(id)) {
            throw new RuntimeException("Hall not found with id: " + id);
        }

        hallRepository.deleteById(id);
        log.info("Hall deleted with ID: {}", id);
    }

    private void generateSeatsForHall(Hall hall, List<String> vipRows) {
        log.info("Generating seats for hall: {}", hall.getName());

        List<Seat> seats = new ArrayList<>();
        String[] rowLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        for (int row = 0; row < hall.getTotalRows(); row++) {
            String rowLetter = rowLetters[row];
            boolean isVipRow = vipRows != null && vipRows.contains(rowLetter);

            for (int col = 1; col <= hall.getTotalColumns(); col++) {
                Seat seat = new Seat();
                seat.setHall(hall);
                seat.setRowLetter(rowLetter);
                seat.setSeatNumber(col);
                seat.setSeatType(isVipRow ? SeatType.VIP : SeatType.REGULAR);
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        log.info("Generated {} seats for hall: {}", seats.size(), hall.getName());
    }

    private HallDto convertToDto(Hall hall) {
        HallDto dto = new HallDto();
        dto.setId(hall.getId());
        dto.setName(hall.getName());
        dto.setTotalRows(hall.getTotalRows());
        dto.setTotalColumns(hall.getTotalColumns());
        dto.setTotalSeats(hall.getTotalRows() * hall.getTotalColumns());
        return dto;
    }
}
