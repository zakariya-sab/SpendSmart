package Um6p.Project.DigitalCulture.SpendSmart.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String color;
    @OneToMany(mappedBy = "category")
    private Collection<Expense> expenses = new ArrayList<>();
    @OneToMany(mappedBy = "category")
    private Collection<Budget> budgets = new ArrayList<>();
}
