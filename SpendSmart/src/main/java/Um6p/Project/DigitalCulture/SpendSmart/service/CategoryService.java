package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.CategoryDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling all category-related business logic.
 * Manages the creation and retrieval of spending categories.
 * Also initializes a default set of categories at application startup.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    /** Repository for category database operations */
    private final CategoryRepository categoryRepository;

    /**
     * Retrieve all available spending categories from the database.
     *
     * @return a list of CategoryDTO objects representing all categories
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new spending category.
     * Validates that no category with the same name already exists.
     *
     * @param dto the category data containing name, color, and icon
     * @return the saved category as a CategoryDTO
     * @throws RuntimeException if a category with the same name already exists
     */
    public CategoryDTO createCategory(CategoryDTO dto) {
        // Check if a category with this name already exists to prevent duplicates
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category with name '" + dto.getName() + "' already exists");
        }

        // Build and save the new category entity
        Category category = Category.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .icon(dto.getIcon())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category: {}", savedCategory.getName());
        return mapToDTO(savedCategory);
    }

    /**
     * Initialize the default set of spending categories if none exist.
     * Called at application startup via CommandLineRunner.
     * Creates 6 default categories: Food, Transport, Health, Entertainment, Shopping, Other.
     */
    public void initDefaultCategories() {
        // Only create defaults if the categories table is empty
        if (categoryRepository.count() == 0) {
            log.info("Initializing default categories...");

            // Define default categories with their names, colors, and icons
            Object[][] defaults = {
                {"Food",          "#FF6B6B", "restaurant"},
                {"Transport",     "#4ECDC4", "directions_car"},
                {"Health",        "#45B7D1", "local_hospital"},
                {"Entertainment", "#96CEB4", "movie"},
                {"Shopping",      "#FFEAA7", "shopping_cart"},
                {"Other",         "#DDA0DD", "category"}
            };

            // Create and save each default category
            for (Object[] categoryData : defaults) {
                Category category = Category.builder()
                        .name((String) categoryData[0])
                        .color((String) categoryData[1])
                        .icon((String) categoryData[2])
                        .build();
                categoryRepository.save(category);
                log.info("Created default category: {}", categoryData[0]);
            }
        }
    }

    /**
     * Convert a Category entity to a CategoryDTO for API responses.
     *
     * @param category the Category entity to convert
     * @return a CategoryDTO with the entity's data
     */
    public CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .icon(category.getIcon())
                .build();
    }
}
