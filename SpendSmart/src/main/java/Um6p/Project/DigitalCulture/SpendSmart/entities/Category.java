package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity representing a spending category (e.g. Food, Transport, Health).
 * Categories are shared across all users and are used to group expenses and budgets.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display name of the category (e.g. "Food", "Transport") */
    @Column(unique = true, nullable = false)
    private String name;

    /** Hex color code used to visually represent this category (e.g. "#FF6B6B") */
    private String color;

    /** Icon name used in the UI to represent this category (e.g. "restaurant") */
    private String icon;

    /** All expenses that belong to this category */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Expense> expenses = new ArrayList<>();

    /** All budgets that are associated with this category */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Budget> budgets = new ArrayList<>();
}
