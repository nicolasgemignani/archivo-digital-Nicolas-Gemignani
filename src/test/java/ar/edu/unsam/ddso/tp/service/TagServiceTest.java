package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.TagRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.BusinessRuleException;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;

    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagService(tagRepository);
    }

    @Test
    void createSavesTagAndReturnsDto() {
        // Given
        when(tagRepository.existsByName("photo")).thenReturn(false);
        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(
                        invocation -> {
                            Tag tag = invocation.getArgument(0);
                            tag.setId(1L);
                            return tag;
                        });

        // When
        TagResponseDTO result = tagService.create(new TagRequestDTO("photo"));

        // Then
        assertEquals(1L, result.id());
        assertEquals("photo", result.name());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        // Given
        when(tagRepository.existsByName("photo")).thenReturn(true);

        // When / Then
        assertThrows(
                BusinessRuleException.class, () -> tagService.create(new TagRequestDTO("photo")));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void deleteThrowsWhenTagIsInUse() {
        // Given
        Tag tag = new Tag(1L, "photo");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.isInUse(1L)).thenReturn(true);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> tagService.delete(1L));
        verify(tagRepository, never()).delete(any(Tag.class));
    }

    @Test
    void deleteThrowsWhenTagDoesNotExist() {
        // Given
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> tagService.delete(99L));
    }
}