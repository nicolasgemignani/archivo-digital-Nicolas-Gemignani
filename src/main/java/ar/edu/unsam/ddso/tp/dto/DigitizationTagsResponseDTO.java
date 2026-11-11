package ar.edu.unsam.ddso.tp.dto;

import java.util.List;

public record DigitizationTagsResponseDTO(
        Long id, String url, String description, List<TagResponseDTO> tags) {}