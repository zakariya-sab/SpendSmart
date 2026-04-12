package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Budget;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserAndCategoryAndMonth(User user, Category category, String month);

    List<Budget> findByUserAndMonth(User user, String month);
}
