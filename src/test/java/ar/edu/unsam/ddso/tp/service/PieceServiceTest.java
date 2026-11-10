package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitizationRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PieceServiceTest {

    private PieceRepository pieceRepository;
    private DigitizationRepository digitizationRepository;
    private PieceService pieceService;

    @BeforeEach
    void setUp() {
        pieceRepository = mock(PieceRepository.class);
        digitizationRepository = mock(DigitizationRepository.class);
        pieceService = new PieceService(pieceRepository, digitizationRepository);
    }

    @Test
    void getAllPiecesReturnsMappedPieces() {
        Piece piece = createPiece(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Piece> pieces = new PageImpl<>(List.of(piece), pageable, 1);

        when(pieceRepository.findAll(pageable)).thenReturn(pieces);

        Page<PieceResponseDTO> result = pieceService.getAllPieces(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
        assertEquals("Fotografia", result.getContent().get(0).name());
    }

    @Test
    void getPieceByIdReturnsPiece() {
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(createPiece(1L)));

        PieceResponseDTO result = pieceService.getPieceById(1L);

        assertEquals(1L, result.id());
        assertEquals("Fotografia", result.name());
    }

    @Test
    void getPieceByIdThrowsWhenMissing() {
        when(pieceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pieceService.getPieceById(99L));
    }

    @Test
    void createPieceSavesRequestData() {
        LocalDate date = LocalDate.of(2024, 5, 10);
        PieceRequestDTO request = new PieceRequestDTO("Fotografia", "Archivo", date);
        when(pieceRepository.save(any(Piece.class))).thenAnswer(invocation -> {
            Piece saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        PieceResponseDTO result = pieceService.createPiece(request);

        ArgumentCaptor<Piece> captor = ArgumentCaptor.forClass(Piece.class);
        verify(pieceRepository).save(captor.capture());
        assertEquals("Fotografia", captor.getValue().getName());
        assertEquals("Archivo", captor.getValue().getDescription());
        assertEquals(date, captor.getValue().getDate());
        assertEquals(1L, result.id());
    }

    @Test
    void addDigitizationAssociatesItWithPiece() {
        Piece piece = createPiece(1L);
        DigitizationRequestDTO request = new DigitizationRequestDTO(
                "https://example.com/file", "Escaneo");
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));
        when(digitizationRepository.save(any(Digitization.class))).thenAnswer(invocation -> {
            Digitization saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        DigitizationResponseDTO result = pieceService.addDigitizationToPiece(1L, request);

        ArgumentCaptor<Digitization> captor = ArgumentCaptor.forClass(Digitization.class);
        verify(digitizationRepository).save(captor.capture());
        assertSame(piece, captor.getValue().getPiece());
        assertEquals(request.url(), captor.getValue().getUrl());
        assertEquals(5L, result.id());
    }

    private Piece createPiece(Long id) {
        Piece piece = new Piece();
        piece.setId(id);
        piece.setName("Fotografia");
        piece.setDescription("Archivo");
        piece.setDate(LocalDate.of(2024, 5, 10));
        return piece;
    }
}
