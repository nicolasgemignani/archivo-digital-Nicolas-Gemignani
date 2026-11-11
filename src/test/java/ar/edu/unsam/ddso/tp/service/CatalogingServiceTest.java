package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.DigitizationTagsResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogingServiceTest {

    @Mock private DigitizationRepository digitizationRepository;
    @Mock private TagRepository tagRepository;

    private CatalogingService catalogingService;
    private Digitization digitization;
    private Tag photo;

    @BeforeEach
    void setUp() {
        catalogingService = new CatalogingService(digitizationRepository, tagRepository);
        digitization = new Digitization();
        digitization.setId(10L);
        digitization.setUrl("http://example.com/scan1.jpg");
        photo = new Tag(1L, "photo");
    }

    @Test
    void assignTagAddsTagToDigitization() {
        // Given
        when(digitizationRepository.findById(10L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(digitizationRepository.save(any(Digitization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DigitizationTagsResponseDTO result = catalogingService.assignTag(10L, 1L);

        // Then
        assertEquals(1, result.tags().size());
        assertEquals("photo", result.tags().get(0).name());
    }

    @Test
    void assignTagTwiceDoesNotDuplicateTag() {
        // Given
        digitization.getTags().add(photo);
        when(digitizationRepository.findById(10L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(digitizationRepository.save(any(Digitization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DigitizationTagsResponseDTO result = catalogingService.assignTag(10L, 1L);

        // Then
        assertEquals(1, result.tags().size());
    }

    @Test
    void assignTagThrowsWhenDigitizationDoesNotExist() {
        // Given
        when(digitizationRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> catalogingService.assignTag(99L, 1L));
        verifyNoInteractions(tagRepository);
    }

    @Test
    void assignTagThrowsWhenTagDoesNotExist() {
        // Given
        when(digitizationRepository.findById(10L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> catalogingService.assignTag(10L, 99L));
        verify(digitizationRepository, never()).save(any(Digitization.class));
    }

    @Test
    void removeTagRemovesAssignedTag() {
        // Given
        digitization.getTags().add(photo);
        when(digitizationRepository.findById(10L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(digitizationRepository.save(any(Digitization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DigitizationTagsResponseDTO result = catalogingService.removeTag(10L, 1L);

        // Then
        assertEquals(0, result.tags().size());
    }

    @Test
    void removeTagDoesNothingWhenTagWasNotAssigned() {
        // Given
        when(digitizationRepository.findById(10L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(digitizationRepository.save(any(Digitization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DigitizationTagsResponseDTO result = catalogingService.removeTag(10L, 1L);

        // Then
        assertEquals(0, result.tags().size());
    }
}