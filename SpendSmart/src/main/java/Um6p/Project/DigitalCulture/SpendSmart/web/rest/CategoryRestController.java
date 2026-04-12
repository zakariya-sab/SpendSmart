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

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponseDTO.success("Categories retrieved successfully", categories));
    }

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
