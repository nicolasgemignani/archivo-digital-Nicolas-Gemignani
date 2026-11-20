package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class PieceSpecifications {

    private PieceSpecifications() {}

    public static Specification<Piece> withFilters(
            String name, String description, LocalDate dateFrom, LocalDate dateTo, List<String> tags) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(containsIgnoreCase(cb, root.get("name"), name));
            }
            if (description != null) {
                predicates.add(containsIgnoreCase(cb, root.get("description"), description));
            }
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<LocalDate>get("date"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<LocalDate>get("date"), dateTo));
            }
            // the piece must have every tag (in any of its digitizations)
            for (String tag : tags) {
                predicates.add(cb.exists(hasDigitizationWithTag(root, query, cb, tag)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate containsIgnoreCase(
            CriteriaBuilder cb, Path<String> field, String value) {
        return cb.like(cb.lower(field), "%" + value.toLowerCase(Locale.ROOT) + "%");
    }

    private static Subquery<Long> hasDigitizationWithTag(
            Root<Piece> piece, CriteriaQuery<?> query, CriteriaBuilder cb, String tagName) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Digitization> digitization = subquery.from(Digitization.class);
        Join<Digitization, Tag> tag = digitization.join("tags");
        subquery.select(digitization.get("id"))
                .where(
                        cb.equal(digitization.get("piece"), piece),
                        cb.equal(cb.lower(tag.get("name")), tagName.toLowerCase(Locale.ROOT)));
        return subquery;
    }
}
