package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.DigitizationTagsResponseDTO;
import ar.edu.unsam.ddso.tp.service.CatalogingService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/digitizations/{digitizationId}/tags/{tagId}")
public class CatalogingController {

    private final CatalogingService catalogingService;

    public CatalogingController(CatalogingService catalogingService) {
        this.catalogingService = catalogingService;
    }

    @PutMapping
    public DigitizationTagsResponseDTO assignTag(
            @PathVariable Long digitizationId, @PathVariable Long tagId) {
        return catalogingService.assignTag(digitizationId, tagId);
    }

    @DeleteMapping
    public DigitizationTagsResponseDTO removeTag(
            @PathVariable Long digitizationId, @PathVariable Long tagId) {
        return catalogingService.removeTag(digitizationId, tagId);
    }
}