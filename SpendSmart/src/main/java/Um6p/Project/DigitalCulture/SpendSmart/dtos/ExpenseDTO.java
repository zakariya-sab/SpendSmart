package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Date is required")
    private Date date;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;
    private String categoryColor;
}
