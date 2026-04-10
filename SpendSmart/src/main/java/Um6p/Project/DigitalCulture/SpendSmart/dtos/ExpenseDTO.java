package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Data Transfer Object for Expense creation and retrieval.
 * Used to receive expense data from the client and to send expense data in responses.
 * The category is referenced by its ID to avoid circular serialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    /** Expense ID — present in responses, not needed in creation requests */
    private Long id;

    /** Monetary amount of the expense — must be a positive number */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    /** Date when the expense occurred — required */
    @NotNull(message = "Date is required")
    private Date date;

    /** Optional description or note about the expense */
    private String description;

    /** ID of the category this expense belongs to */
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    /** Category name — returned in responses for display purposes */
    private String categoryName;

    /** Category color hex code — returned in responses for display purposes */
    private String categoryColor;
}
