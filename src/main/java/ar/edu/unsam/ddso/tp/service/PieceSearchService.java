package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PageResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.PieceSearchRequestDTO;
import ar.edu.unsam.ddso.tp.exception.BadRequestException;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import ar.edu.unsam.ddso.tp.repository.PieceSpecifications;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PieceSearchService {

    public static final int MAX_PAGE_SIZE = 100;

    private final PieceRepository pieceRepository;

    public PieceSearchService(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    // ---------- Queries ----------

    @Transactional(readOnly = true)
    public PageResponseDTO<PieceResponseDTO> search(
            PieceSearchRequestDTO request, int page, int size) {
        validatePagination(page, size);
        if (request.dateFrom() != null
                && request.dateTo() != null
                && request.dateFrom().isAfter(request.dateTo())) {
            throw new BadRequestException("dateFrom must be before or equal to dateTo");
        }

        Specification<Piece> specification =
                PieceSpecifications.withFilters(
                        normalize(request.name()),
                        normalize(request.description()),
                        request.dateFrom(),
                        request.dateTo(),
                        normalizeTags(request.tags()));
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").and(Sort.by("id")));

        Page<Piece> result = pieceRepository.findAll(specification, pageable);
        return new PageResponseDTO<>(
                result.getContent().stream().map(PieceMapper::toPieceDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    // ---------- Private ----------

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("page must be greater than or equal to 0");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream().map(this::normalize).filter(Objects::nonNull).distinct().toList();
    }
}
