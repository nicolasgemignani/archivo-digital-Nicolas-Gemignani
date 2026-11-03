package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.PieceRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.service.PieceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pieces")
public class PieceController {

    private final PieceService pieceService;

    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @GetMapping
    public ResponseEntity getAllPieces() {
        return ResponseEntity.ok(pieceService.getAllPieces());
    }

    @GetMapping("/{id}")
    public ResponseEntity getPieceById(@PathVariable Long id) {
        return ResponseEntity.ok(pieceService.getPieceById(id));
    }

    @PostMapping
    public ResponseEntity createPiece(@Valid @RequestBody PieceRequestDTO requestDTO) {
        PieceResponseDTO created = pieceService.createPiece(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/digitizations")
    public ResponseEntity addDigitization(
            @PathVariable Long id,
            @Valid @RequestBody DigitizationRequestDTO requestDTO) {
        DigitizationResponseDTO created = pieceService.addDigitizationToPiece(id, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}