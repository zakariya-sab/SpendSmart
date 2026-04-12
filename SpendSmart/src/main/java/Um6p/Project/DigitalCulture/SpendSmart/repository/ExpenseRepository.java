package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Expense;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    List<Expense> findByUserAndCategory(User user, Category category);

    List<Expense> findByUserAndDateBetween(User user, Date start, Date end);
}
