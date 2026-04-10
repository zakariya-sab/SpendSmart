package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Entity representing a single financial expense made by a user.
 * Each expense belongs to a user and is classified under a category.
 */
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Monetary amount of this expense (in the user's currency) */
    private double amount;

    /** Date and time when this expense occurred */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /** Optional text description providing context for the expense */
    private String description;

    /** The user who recorded this expense */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The category this expense belongs to (e.g. Food, Transport) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
