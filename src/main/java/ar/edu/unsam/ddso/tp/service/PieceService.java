package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitizationRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PieceService {

    private final PieceRepository pieceRepository;
    private final DigitizationRepository digitizationRepository;

    public PieceService(PieceRepository pieceRepository,
            DigitizationRepository digitizationRepository) {
        this.pieceRepository = pieceRepository;
        this.digitizationRepository = digitizationRepository;
    }

    public Page<PieceResponseDTO> getAllPieces(Pageable pageable) {
        return pieceRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    public PieceResponseDTO getPieceById(Long id) {
        Piece piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Piece not found with id: " + id));
        return mapToResponseDTO(piece);
    }

    @Transactional(readOnly = true)
    public List<DigitizationResponseDTO> getDigitizationsByPieceId(Long pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Piece not found with id: " + pieceId));

        return piece.getDigitizations().stream()
                .map(d -> new DigitizationResponseDTO(
                        d.getId(), d.getUrl(), d.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PieceResponseDTO createPiece(PieceRequestDTO requestDTO) {
        Piece piece = new Piece();
        piece.setName(requestDTO.name());
        piece.setDescription(requestDTO.description());
        piece.setDate(requestDTO.date());

        Piece savedPiece = pieceRepository.save(piece);
        return mapToResponseDTO(savedPiece);
    }

    @Transactional
    public DigitizationResponseDTO addDigitizationToPiece(
            Long pieceId, DigitizationRequestDTO requestDTO) {
        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new RuntimeException("Piece not found with id: " + pieceId));

        Digitization digitization = new Digitization();
        digitization.setUrl(requestDTO.url());
        digitization.setDescription(requestDTO.description());
        digitization.setPiece(piece);

        Digitization savedDigitization = digitizationRepository.save(digitization);

        return new DigitizationResponseDTO(
                savedDigitization.getId(),
                savedDigitization.getUrl(),
                savedDigitization.getDescription()
        );
    }

    private PieceResponseDTO mapToResponseDTO(Piece piece) {
        List<DigitizationResponseDTO> digiDTOs = piece.getDigitizations().stream()
                .map(d -> new DigitizationResponseDTO(
                        d.getId(), d.getUrl(), d.getDescription()))
                .collect(Collectors.toList());

        return new PieceResponseDTO(
                piece.getId(),
                piece.getName(),
                piece.getDescription(),
                piece.getDate(),
                digiDTOs
        );
    }
}
