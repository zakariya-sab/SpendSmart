package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category creation and retrieval.
 * Used to create new categories and return category data to the client.
 * Category creation is restricted to ADMIN users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    /** Category's unique identifier — present in responses */
    private Long id;

    /** Display name of the category (e.g. "Food", "Transport") — must not be blank */
    @NotBlank(message = "Category name is required")
    private String name;

    /** Hex color code for visual representation (e.g. "#FF6B6B") */
    private String color;

    /** Icon name used in the UI (e.g. "restaurant", "directions_car") */
    private String icon;
}
