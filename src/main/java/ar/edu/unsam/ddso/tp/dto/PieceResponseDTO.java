package ar.edu.unsam.ddso.tp.dto;

import java.time.LocalDate;
import java.util.List;

public record PieceResponseDTO(
    Long id,
    String name,
    String description,
    LocalDate date,
    List<DigitizationResponseDTO> digitizations
) {}