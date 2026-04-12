package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDTO {

    private Long id;

    @NotNull(message = "Max amount is required")
    @Positive(message = "Max amount must be greater than zero")
    private Double maxAmount;

    private Double spentAmount;
    private Double remainingAmount;
    private Double percentageUsed;

    @NotBlank(message = "Month is required")
    private String month;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;
    private String categoryColor;
}
