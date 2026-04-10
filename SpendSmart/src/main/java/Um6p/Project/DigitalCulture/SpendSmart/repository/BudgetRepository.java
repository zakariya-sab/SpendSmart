package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Budget;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Budget entity database operations.
 * Provides custom query methods to find budgets by user, category, and month.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Find a specific budget for a user, category, and month combination.
     * Used to enforce one budget per category per month per user.
     *
     * @param user     the user who owns the budget
     * @param category the spending category
     * @param month    the month in "YYYY-MM" format (e.g. "2026-04")
     * @return an Optional containing the budget if it exists, or empty if not
     */
    Optional<Budget> findByUserAndCategoryAndMonth(User user, Category category, String month);

    /**
     * Find all budgets for a user in a specific month.
     * Used to display the monthly budget overview on the dashboard.
     *
     * @param user  the user whose budgets to retrieve
     * @param month the month in "YYYY-MM" format (e.g. "2026-04")
     * @return list of all budgets for the user in the given month
     */
    List<Budget> findByUserAndMonth(User user, String month);
}
