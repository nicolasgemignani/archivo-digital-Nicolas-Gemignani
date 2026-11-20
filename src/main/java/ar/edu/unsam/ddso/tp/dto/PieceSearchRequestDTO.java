package ar.edu.unsam.ddso.tp.dto;

import java.time.LocalDate;
import java.util.List;

public record PieceSearchRequestDTO(
        String name, String description, LocalDate dateFrom, LocalDate dateTo, List<String> tags) {}
