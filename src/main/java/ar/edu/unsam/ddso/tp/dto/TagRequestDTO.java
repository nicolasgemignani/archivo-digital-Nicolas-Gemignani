package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestDTO(@NotBlank @Size(max = 100) String name) {}