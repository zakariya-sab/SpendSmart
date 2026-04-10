package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.FinancialHealthScore;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FinancialHealthScore entity database operations.
 * Provides methods to find scores by user and month for trend analysis.
 */
@Repository
public interface FInancialHealthScoreRepository extends JpaRepository<FinancialHealthScore, Long> {

    /**
     * Find the financial health score for a specific user and month.
     * Used to check if a score already exists before creating or updating it.
     *
     * @param user  the user whose score to retrieve
     * @param month the month in "YYYY-MM" format (e.g. "2026-04")
     * @return an Optional containing the score if found, or empty if not found
     */
    Optional<FinancialHealthScore> findByUserAndMonth(User user, String month);

    /**
     * Find all financial health scores for a user ordered from newest to oldest.
     * Used to display the score evolution chart in the statistics page.
     *
     * @param user the user whose score history to retrieve
     * @return list of scores ordered by date descending (most recent first)
     */
    List<FinancialHealthScore> findByUserOrderByDateDesc(User user);
}
