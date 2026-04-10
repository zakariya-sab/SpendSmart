package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Data Transfer Object for FinancialHealthScore retrieval.
 * Used to return score data to the client for display on the dashboard
 * and in the statistics charts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDTO {

    /** Score's unique identifier */
    private Long id;

    /** Numerical score from 0 to 100 representing financial health */
    private Double value;

    /** Letter grade: "A" (excellent), "B" (good), or "C" (needs improvement) */
    private String grade;

    /** The month this score applies to in "YYYY-MM" format */
    private String month;

    /** Date and time when the score was calculated */
    private Date date;
}
