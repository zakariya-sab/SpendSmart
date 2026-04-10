package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Entity representing an application user.
 * Each user can have expenses, budgets, alerts, and financial health scores.
 * Passwords are stored in encrypted form (BCrypt).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Primary key, auto-incremented by the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** User's email address — must be unique, used as login identifier */
    @Column(unique = true, nullable = false)
    private String email;

    /** BCrypt-encrypted password — never stored in plain text */
    @Column(nullable = false)
    private String password;

    /** Role of the user: either "USER" or "ADMIN" */
    private String role;

    /** Date when the user account was created */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /** List of expenses recorded by this user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Expense> expenses = new ArrayList<>();

    /** List of budgets defined by this user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Budget> budgets = new ArrayList<>();

    /** List of alerts generated for this user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Alert> alerts = new ArrayList<>();

    /** List of monthly financial health scores for this user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<FinancialHealthScore> scores = new ArrayList<>();
}
