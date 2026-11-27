package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.unsam.ddso.tp.dto.PageResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceSearchRequestDTO;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PieceSearchIntegrationTest {

    @Autowired private PieceRepository pieceRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PieceSearchService pieceSearchService;

    @BeforeEach
    void setUp() {
        Tag photo = tagRepository.save(new Tag(null, "itest-photo"));
        Tag letter = tagRepository.save(new Tag(null, "itest-letter"));

        savePiece("ITest Letter", "Handwritten letter", LocalDate.of(1900, 1, 1), photo, letter);
        savePiece("ITest Map", "Old city map", LocalDate.of(1950, 6, 1), photo);
        savePiece("ITest Book", "Printed book", LocalDate.of(2000, 3, 1));
    }

    @Test
    void searchByNameIgnoresCase() {
        // When
        PageResponseDTO<PieceResponseDTO> result = search("itest letter", null, null, null, null);

        // Then
        assertEquals(List.of("ITest Letter"), names(result));
    }

    @Test
    void searchByDescriptionAndDateRange() {
        // When
        PageResponseDTO<PieceResponseDTO> byDescription = search("itest", "MAP", null, null, null);
        PageResponseDTO<PieceResponseDTO> byDates =
                search("itest", null, LocalDate.of(1940, 1, 1), LocalDate.of(1999, 12, 31), null);

        // Then
        assertEquals(List.of("ITest Map"), names(byDescription));
        assertEquals(List.of("ITest Map"), names(byDates));
    }

    @Test
    void pieceMatchesTagIfAnyDigitizationHasIt() {
        // When
        PageResponseDTO<PieceResponseDTO> result =
                search(null, null, null, null, List.of("itest-photo"));

        // Then
        assertEquals(List.of("ITest Letter", "ITest Map"), names(result));
    }

    @Test
    void withSeveralTagsPieceMustHaveAllOfThem() {
        // When
        PageResponseDTO<PieceResponseDTO> result =
                search(null, null, null, null, List.of("itest-photo", "ITEST-LETTER"));

        // Then
        assertEquals(List.of("ITest Letter"), names(result));
    }

    @Test
    void searchIsPaginated() {
        // When
        PageResponseDTO<PieceResponseDTO> firstPage =
                pieceSearchService.search(
                        new PieceSearchRequestDTO("itest", null, null, null, null), 0, 2);

        // Then
        assertEquals(List.of("ITest Book", "ITest Letter"), names(firstPage));
        assertEquals(3, firstPage.totalElements());
        assertEquals(2, firstPage.totalPages());
    }

    @Test
    void searchWithoutMatchesReturnsEmptyList() {
        // When
        PageResponseDTO<PieceResponseDTO> result = search("does-not-exist", null, null, null, null);

        // Then
        assertTrue(result.content().isEmpty());
    }

    private PageResponseDTO<PieceResponseDTO> search(
            String name, String description, LocalDate from, LocalDate to, List<String> tags) {
        return pieceSearchService.search(
                new PieceSearchRequestDTO(name, description, from, to, tags), 0, 10);
    }

    private List<String> names(PageResponseDTO<PieceResponseDTO> page) {
        return page.content().stream().map(PieceResponseDTO::name).toList();
    }

    private void savePiece(String name, String description, LocalDate date, Tag... tags) {
        Piece piece = new Piece(null, name, description, date, new ArrayList<>());
        Digitization digitization = new Digitization();
        digitization.setUrl("http://example.com/" + name.replace(' ', '-') + ".jpg");
        digitization.setPiece(piece);
        digitization.getTags().addAll(List.of(tags));
        piece.getDigitizations().add(digitization);
        pieceRepository.save(piece);
    }
}
