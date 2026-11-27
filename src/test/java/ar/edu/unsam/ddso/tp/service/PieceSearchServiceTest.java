package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.PageResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceSearchRequestDTO;
import ar.edu.unsam.ddso.tp.exception.BadRequestException;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PieceSearchServiceTest {

    @Mock private PieceRepository pieceRepository;

    private PieceSearchService pieceSearchService;

    @BeforeEach
    void setUp() {
        pieceSearchService = new PieceSearchService(pieceRepository);
    }

    @Test
    void searchReturnsMatchingPiecesAsPage() {
        // Given
        Piece letter = createPiece(1L, "Letter");
        Digitization scan = new Digitization();
        scan.setId(10L);
        scan.setUrl("http://example.com/letter.jpg");
        scan.setPiece(letter);
        letter.getDigitizations().add(scan);
        when(pieceRepository.findAll(
                        ArgumentMatchers.<Specification<Piece>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(letter), PageRequest.of(0, 5), 1));
        PieceSearchRequestDTO request = new PieceSearchRequestDTO("let", null, null, null, null);

        // When
        PageResponseDTO<PieceResponseDTO> result = pieceSearchService.search(request, 0, 5);

        // Then
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals("Letter", result.content().get(0).name());
        assertEquals(
                "http://example.com/letter.jpg",
                result.content().get(0).digitizations().get(0).url());
    }

    @Test
    void searchAsksRepositoryForRequestedPageSortedByName() {
        // Given
        when(pieceRepository.findAll(
                        ArgumentMatchers.<Specification<Piece>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 20), 0));
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        // When
        pieceSearchService.search(emptyRequest(), 2, 20);

        // Then
        verify(pieceRepository)
                .findAll(ArgumentMatchers.<Specification<Piece>>any(), pageable.capture());
        assertEquals(2, pageable.getValue().getPageNumber());
        assertEquals(20, pageable.getValue().getPageSize());
        assertEquals(
                Sort.Direction.ASC,
                pageable.getValue().getSort().getOrderFor("name").getDirection());
    }

    @Test
    void searchWithoutResultsReturnsEmptyPageInsteadOfError() {
        // Given
        when(pieceRepository.findAll(
                        ArgumentMatchers.<Specification<Piece>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        // When
        PageResponseDTO<PieceResponseDTO> result =
                pieceSearchService.search(
                        new PieceSearchRequestDTO("nothing", null, null, null, List.of("x")),
                        0,
                        10);

        // Then
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
    }

    @Test
    void searchRejectsDateFromAfterDateTo() {
        // Given
        PieceSearchRequestDTO request =
                new PieceSearchRequestDTO(
                        null, null, LocalDate.of(2000, 1, 1), LocalDate.of(1990, 1, 1), null);

        // When / Then
        assertThrows(BadRequestException.class, () -> pieceSearchService.search(request, 0, 10));
        verifyNoInteractions(pieceRepository);
    }

    @Test
    void searchRejectsInvalidPagination() {
        // When / Then
        assertThrows(
                BadRequestException.class, () -> pieceSearchService.search(emptyRequest(), -1, 10));
        assertThrows(
                BadRequestException.class, () -> pieceSearchService.search(emptyRequest(), 0, 0));
        assertThrows(
                BadRequestException.class, () -> pieceSearchService.search(emptyRequest(), 0, 101));
        verifyNoInteractions(pieceRepository);
    }

    private PieceSearchRequestDTO emptyRequest() {
        return new PieceSearchRequestDTO(null, null, null, null, null);
    }

    private Piece createPiece(Long id, String name) {
        return new Piece(id, name, "Description", LocalDate.of(1900, 1, 1), new ArrayList<>());
    }
}
