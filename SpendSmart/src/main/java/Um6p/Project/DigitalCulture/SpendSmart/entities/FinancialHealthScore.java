package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/** Score 0–100; grade "A" (90-100), "B" (60-89), "C" (0-59). Recalculated on every expense change. */
@Entity
@Table(name = "financial_health_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialHealthScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double value;
    private String grade;

    @Column(name = "score_month")
    private String month;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
