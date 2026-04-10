package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity representing a monthly spending budget for a specific category.
 * Tracks both the maximum allowed amount and how much has been spent.
 * Alerts are generated when spending approaches or exceeds the budget limit.
 */
@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Maximum spending limit set for this budget period */
    private double maxAmount;

    /** Current total amount spent so far in this budget period */
    private double spentAmount;

    /** The month this budget applies to, in "YYYY-MM" format (e.g. "2026-04") */
    private String month;

    /** The user who owns this budget */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The category this budget is allocated to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Alerts that have been generated for this budget (WARNING or EXCEEDED) */
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Alert> alerts = new ArrayList<>();
}
