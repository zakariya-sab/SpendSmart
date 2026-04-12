package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.CategoryDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category with name '" + dto.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .icon(dto.getIcon())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category: {}", savedCategory.getName());
        return mapToDTO(savedCategory);
    }

    /** Seeds 6 default categories at startup if the table is empty. */
    public void initDefaultCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Initializing default categories...");

            Object[][] defaults = {
                {"Food",          "#FF6B6B", "restaurant"},
                {"Transport",     "#4ECDC4", "directions_car"},
                {"Health",        "#45B7D1", "local_hospital"},
                {"Entertainment", "#96CEB4", "movie"},
                {"Shopping",      "#FFEAA7", "shopping_cart"},
                {"Other",         "#DDA0DD", "category"}
            };

            for (Object[] categoryData : defaults) {
                Category category = Category.builder()
                        .name((String) categoryData[0])
                        .color((String) categoryData[1])
                        .icon((String) categoryData[2])
                        .build();
                categoryRepository.save(category);
            }
            log.info("Default categories created.");
        }
    }

    public CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .icon(category.getIcon())
                .build();
    }
}
