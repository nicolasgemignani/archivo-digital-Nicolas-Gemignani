package ar.edu.unsam.ddso.tp.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PageResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceSearchRequestDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.service.PieceSearchService;
import ar.edu.unsam.ddso.tp.service.PieceService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramBotHandlerTest {

    @Mock private PieceSearchService pieceSearchService;
    @Mock private PieceService pieceService;

    private TelegramBotHandler handler;
    private PieceResponseDTO letter;

    @BeforeEach
    void setUp() {
        handler = new TelegramBotHandler(pieceSearchService, pieceService);
        letter =
                new PieceResponseDTO(
                        3L,
                        "San Martin letter",
                        "Handwritten letter",
                        LocalDate.of(1817, 5, 10),
                        List.of(
                                new DigitizationResponseDTO(
                                        10L, "http://example.com/letter.jpg", "Front side")));
    }

    @Test
    void startShowsHelp() {
        // When
        String reply = handler.handle("/start");

        // Then
        assertTrue(reply.contains("/buscar"));
        verifyNoInteractions(pieceSearchService, pieceService);
    }

    @Test
    void plainTextSearchesPiecesByName() {
        // Given
        when(pieceSearchService.search(any(), eq(0), eq(TelegramBotHandler.RESULTS_PER_MESSAGE)))
                .thenReturn(new PageResponseDTO<>(List.of(letter), 0, 5, 1, 1));
        ArgumentCaptor<PieceSearchRequestDTO> request =
                ArgumentCaptor.forClass(PieceSearchRequestDTO.class);

        // When
        String reply = handler.handle("letter");

        // Then
        verify(pieceSearchService)
                .search(request.capture(), eq(0), eq(TelegramBotHandler.RESULTS_PER_MESSAGE));
        assertEquals("letter", request.getValue().name());
        assertTrue(reply.contains("San Martin letter"));
        assertTrue(reply.contains("/pieza_3"));
    }

    @Test
    void tagCommandSearchesByTag() {
        // Given
        when(pieceSearchService.search(any(), eq(0), eq(TelegramBotHandler.RESULTS_PER_MESSAGE)))
                .thenReturn(new PageResponseDTO<>(List.of(), 0, 5, 0, 0));
        ArgumentCaptor<PieceSearchRequestDTO> request =
                ArgumentCaptor.forClass(PieceSearchRequestDTO.class);

        // When
        String reply = handler.handle("/tag photo");

        // Then
        verify(pieceSearchService)
                .search(request.capture(), eq(0), eq(TelegramBotHandler.RESULTS_PER_MESSAGE));
        assertEquals(List.of("photo"), request.getValue().tags());
        assertTrue(reply.startsWith("No encontré piezas"));
    }

    @Test
    void pieceCommandShowsDetailWithDigitizationLinks() {
        // Given
        when(pieceService.getPieceById(3L)).thenReturn(letter);

        // When
        String withSpace = handler.handle("/pieza 3");
        String withUnderscore = handler.handle("/pieza_3");

        // Then
        assertTrue(withSpace.contains("San Martin letter"));
        assertTrue(withSpace.contains("Handwritten letter"));
        assertTrue(withSpace.contains("http://example.com/letter.jpg"));
        assertEquals(withSpace, withUnderscore);
    }

    @Test
    void pieceCommandWithUnknownIdSaysItDoesNotExist() {
        // Given
        when(pieceService.getPieceById(7L))
                .thenThrow(new ResourceNotFoundException("Piece not found with id: 7"));

        // When
        String reply = handler.handle("/pieza 7");

        // Then
        assertEquals("No existe la pieza 7.", reply);
    }

    @Test
    void pieceCommandWithInvalidIdShowsUsage() {
        // When
        String reply = handler.handle("/pieza abc");

        // Then
        assertTrue(reply.startsWith("Usá: /pieza"));
        verifyNoInteractions(pieceService);
    }
}
