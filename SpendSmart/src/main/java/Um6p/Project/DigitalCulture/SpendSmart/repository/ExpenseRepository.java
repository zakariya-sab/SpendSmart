package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Expense;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Expense entity database operations.
 * Provides custom query methods in addition to standard JpaRepository methods.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all expenses belonging to a specific user.
     * Returns all expenses regardless of date or category.
     *
     * @param user the user whose expenses to retrieve
     * @return list of all expenses for the given user
     */
    List<Expense> findByUser(User user);

    /**
     * Find all expenses for a specific user and category.
     * Used to calculate category-specific spending totals.
     *
     * @param user     the user whose expenses to retrieve
     * @param category the category to filter by
     * @return list of expenses matching the user and category
     */
    List<Expense> findByUserAndCategory(User user, Category category);

    /**
     * Find all expenses for a user within a date range.
     * Used to filter expenses by month (start = first day, end = last day of month).
     *
     * @param user  the user whose expenses to retrieve
     * @param start the start date (inclusive)
     * @param end   the end date (inclusive)
     * @return list of expenses within the specified date range
     */
    List<Expense> findByUserAndDateBetween(User user, Date start, Date end);
}
