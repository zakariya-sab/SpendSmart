package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.CategoryDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category management operations.
 * GET /api/categories is accessible to all authenticated users.
 * POST /api/categories is restricted to ADMIN users only.
 * Base path: /api/categories
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CategoryRestController {

    /** Service containing category business logic */
    private final CategoryService categoryService;

    /**
     * Get all available spending categories.
     * Available to all authenticated users — used to populate dropdowns in the UI.
     *
     * GET /api/categories
     *
     * @return 200 with a list of all CategoryDTOs
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponseDTO.success("Categories retrieved successfully", categories));
    }

    /**
     * Create a new spending category.
     * Restricted to ADMIN users only (enforced by @PreAuthorize annotation).
     *
     * POST /api/categories
     *
     * @param categoryDTO the category data (name, color, icon)
     * @return 200 with the created CategoryDTO, or 400 if name already exists
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CategoryDTO>> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        try {
            CategoryDTO created = categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok(ApiResponseDTO.success("Category created successfully", created));
        } catch (RuntimeException e) {
            log.error("Failed to create category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }
}
