package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PieceRequestDTO(
    @NotBlank(message = "Name cannot be blank")
    String name,

    String description,

    @NotNull(message = "Date cannot be null")
    LocalDate date
) {}