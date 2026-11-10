package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.DigitizationRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.service.PieceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pieces")
public class PieceController {

    private final PieceService pieceService;

    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @GetMapping
    public ResponseEntity<Page<PieceResponseDTO>> getAllPieces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(pieceService.getAllPieces(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceResponseDTO> getPieceById(@PathVariable Long id) {
        return ResponseEntity.ok(pieceService.getPieceById(id));
    }

    @PostMapping
    public ResponseEntity<PieceResponseDTO> createPiece(
            @Valid @RequestBody PieceRequestDTO requestDTO) {
        PieceResponseDTO created = pieceService.createPiece(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/digitizations")
    public ResponseEntity<DigitizationResponseDTO> addDigitization(
            @PathVariable Long id,
            @Valid @RequestBody DigitizationRequestDTO requestDTO) {
        DigitizationResponseDTO created = pieceService.addDigitizationToPiece(id, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
