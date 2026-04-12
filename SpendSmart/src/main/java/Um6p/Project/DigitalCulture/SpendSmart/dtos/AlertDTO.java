package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {

    private Long id;
    private String message;
    private Date date;
    private boolean isRead;
    private String type;
    private String categoryName;
    private String budgetMonth;
}
