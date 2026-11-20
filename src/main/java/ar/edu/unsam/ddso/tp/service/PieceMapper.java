package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import java.util.List;

final class PieceMapper {

    private PieceMapper() {}

    static PieceResponseDTO toPieceDto(Piece piece) {
        List<DigitizationResponseDTO> digitizations =
                piece.getDigitizations().stream().map(PieceMapper::toDigitizationDto).toList();
        return new PieceResponseDTO(
                piece.getId(),
                piece.getName(),
                piece.getDescription(),
                piece.getDate(),
                digitizations);
    }

    static DigitizationResponseDTO toDigitizationDto(Digitization digitization) {
        return new DigitizationResponseDTO(
                digitization.getId(), digitization.getUrl(), digitization.getDescription());
    }
}
