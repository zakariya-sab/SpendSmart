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
public class ScoreDTO {

    private Long id;
    private Double value;
    private String grade;
    private String month;
    private Date date;
}
