package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotBlank;

public record DigitizationRequestDTO(
    @NotBlank(message = "URL cannot be blank")
    String url,

    String description
) {}