package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String FirstName;
    private String LastName;
    private String Email;
    private String Password;
    @OneToMany(mappedBy = "user")
    private Collection<FinancialHealthScore> financialHealthScores= new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private Collection<Expense> expenses= new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private Collection<Budget> budgets= new ArrayList<>();
}
