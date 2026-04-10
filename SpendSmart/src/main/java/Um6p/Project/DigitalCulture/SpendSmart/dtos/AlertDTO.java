package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Data Transfer Object for Alert retrieval.
 * Used to send alert data to the client without exposing the entity directly.
 * Alerts are read-only from the client's perspective — they are created by the server.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {

    /** Alert's unique identifier */
    private Long id;

    /** Human-readable message describing the alert */
    private String message;

    /** Date and time when this alert was created */
    private Date date;

    /** Whether the user has acknowledged this alert */
    private boolean isRead;

    /** Alert type: "WARNING" (80%+ budget used) or "EXCEEDED" (100%+ budget used) */
    private String type;

    /** Name of the budget category that triggered this alert */
    private String categoryName;

    /** Budget month in "YYYY-MM" format for context */
    private String budgetMonth;
}
