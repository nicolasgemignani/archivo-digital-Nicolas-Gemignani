package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitizationTagsResponseDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogingService {

    private final DigitizationRepository digitizationRepository;
    private final TagRepository tagRepository;

    public CatalogingService(
            DigitizationRepository digitizationRepository, TagRepository tagRepository) {
        this.digitizationRepository = digitizationRepository;
        this.tagRepository = tagRepository;
    }

    // ---------- Operations ----------

    @Transactional
    public DigitizationTagsResponseDTO assignTag(Long digitizationId, Long tagId) {
        Digitization digitization = findDigitizationOrThrow(digitizationId);
        Tag tag = findTagOrThrow(tagId);
        boolean alreadyAssigned = hasTag(digitization, tagId);
        if (!alreadyAssigned) {
            digitization.getTags().add(tag);
        }
        return toDto(digitizationRepository.save(digitization));
    }

    @Transactional
    public DigitizationTagsResponseDTO removeTag(Long digitizationId, Long tagId) {
        Digitization digitization = findDigitizationOrThrow(digitizationId);
        findTagOrThrow(tagId);
        digitization.getTags().removeIf(tag -> tag.getId().equals(tagId));
        return toDto(digitizationRepository.save(digitization));
    }

    // ---------- Private ----------

    private Digitization findDigitizationOrThrow(Long id) {
        return digitizationRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Digitization " + id + " not found"));
    }

    private Tag findTagOrThrow(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag " + id + " not found"));
    }

    private boolean hasTag(Digitization digitization, Long tagId) {
        return digitization.getTags().stream().anyMatch(tag -> tag.getId().equals(tagId));
    }

    private DigitizationTagsResponseDTO toDto(Digitization digitization) {
        List<TagResponseDTO> tags =
                digitization.getTags().stream()
                        .sorted(Comparator.comparing(Tag::getName))
                        .map(tag -> new TagResponseDTO(tag.getId(), tag.getName()))
                        .toList();
        return new DigitizationTagsResponseDTO(
                digitization.getId(), digitization.getUrl(), digitization.getDescription(), tags);
    }
}