package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Budget creation and retrieval.
 * Used to receive budget data from the client and to send budget data in responses.
 * Includes remaining amount and percentage used for dashboard display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDTO {

    /** Budget ID — present in responses, not needed in creation requests */
    private Long id;

    /** Maximum spending limit for this budget — must be positive */
    @NotNull(message = "Max amount is required")
    @Positive(message = "Max amount must be greater than zero")
    private Double maxAmount;

    /** Amount spent so far in this budget period — returned in responses */
    private Double spentAmount;

    /** Remaining amount (maxAmount - spentAmount) — calculated and returned in responses */
    private Double remainingAmount;

    /** Percentage of budget used (spentAmount / maxAmount * 100) — for progress bar display */
    private Double percentageUsed;

    /** The month this budget applies to — must be in "YYYY-MM" format (e.g. "2026-04") */
    @NotBlank(message = "Month is required")
    private String month;

    /** ID of the category this budget is allocated to */
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    /** Category name — returned in responses for display purposes */
    private String categoryName;

    /** Category color hex code — returned for progress bar color coding */
    private String categoryColor;
}
