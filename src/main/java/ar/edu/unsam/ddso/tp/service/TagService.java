package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.TagRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.BusinessRuleException;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // ---------- Queries ----------

    public List<TagResponseDTO> findAll() {
        return tagRepository.findAll().stream().map(this::toDto).toList();
    }

    // ---------- Operations ----------

    @Transactional
    public TagResponseDTO create(TagRequestDTO request) {
        String name = request.name().trim();
        if (tagRepository.existsByName(name)) {
            throw new BusinessRuleException("Tag name already exists: " + name);
        }
        Tag tag = new Tag();
        tag.setName(name);
        return toDto(tagRepository.save(tag));
    }

    @Transactional
    public TagResponseDTO rename(Long id, TagRequestDTO request) {
        Tag tag = findTagOrThrow(id);
        String name = request.name().trim();
        if (!tag.getName().equals(name) && tagRepository.existsByName(name)) {
            throw new BusinessRuleException("Tag name already exists: " + name);
        }
        tag.setName(name);
        return toDto(tagRepository.save(tag));
    }

    @Transactional
    public void delete(Long id) {
        Tag tag = findTagOrThrow(id);
        if (tagRepository.isInUse(id)) {
            throw new BusinessRuleException("Tag is in use and cannot be deleted: " + id);
        }
        tagRepository.delete(tag);
    }

    // ---------- Private ----------

    private Tag findTagOrThrow(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag " + id + " not found"));
    }

    private TagResponseDTO toDto(Tag tag) {
        return new TagResponseDTO(tag.getId(), tag.getName());
    }
}