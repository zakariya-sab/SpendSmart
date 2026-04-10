package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Entity representing a financial alert triggered when a budget threshold is crossed.
 * Alerts are either of type WARNING (80%+ of budget used) or EXCEEDED (100%+ used).
 */
@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable message describing the alert (e.g. "You have spent 85% of your Food budget") */
    private String message;

    /** Date and time when this alert was created */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /** Whether the user has acknowledged/read this alert */
    private boolean isRead;

    /** Alert severity: "WARNING" (approaching limit) or "EXCEEDED" (over limit) */
    private String type;

    /** The user this alert belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The budget that triggered this alert */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;
}
