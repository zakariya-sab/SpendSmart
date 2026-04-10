package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Entity representing the monthly financial health score of a user.
 * The score is calculated based on how well the user is staying within their budgets.
 * Scores range from 0 to 100, with grades A (excellent), B (good), or C (needs improvement).
 */
@Entity
@Table(name = "financial_health_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialHealthScore {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numerical score from 0 to 100 representing financial health */
    private double value;

    /** Letter grade: "A" (90-100), "B" (60-89), or "C" (0-59) */
    private String grade;

    /** The month this score applies to, in "YYYY-MM" format (e.g. "2026-04") */
    private String month;

    /** Date and time when this score was calculated or last updated */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /** The user this financial health score belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
