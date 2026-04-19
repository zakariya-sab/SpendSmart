package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/** alert_type is "WARNING" (≥80% budget used) or "EXCEEDED" (≥100% budget used) */
@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "alert_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "alert_type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;
}
