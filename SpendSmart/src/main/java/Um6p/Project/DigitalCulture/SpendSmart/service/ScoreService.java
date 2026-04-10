package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ScoreDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Budget;
import Um6p.Project.DigitalCulture.SpendSmart.entities.FinancialHealthScore;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.BudgetRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.FInancialHealthScoreRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for calculating and managing financial health scores.
 * Scores are calculated monthly based on how well the user stays within their budgets.
 * Score ranges: A (90-100, avg usage ≤50%), B (60-89, avg usage ≤80%), C (0-59, avg usage >80%).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreService {

    /** Repository for financial health score database operations */
    private final FInancialHealthScoreRepository scoreRepository;

    /** Repository to retrieve budgets for score calculation */
    private final BudgetRepository budgetRepository;

    /** Repository to look up users by email */
    private final UserRepository userRepository;

    /** Formatter for the "YYYY-MM" month string format */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Calculate or update the financial health score for a user in a specific month.
     * The score formula is:
     *   - Average budget usage ≤ 50% → score = 90-100, grade = A
     *   - Average budget usage ≤ 80% → score = 60-89,  grade = B
     *   - Average budget usage > 80%  → score = 0-59,   grade = C
     * If no budgets exist for the month, the score defaults to 100 (A).
     *
     * @param user  the user whose score to calculate
     * @param month the month in "YYYY-MM" format (e.g. "2026-04")
     * @return the saved or updated FinancialHealthScore entity
     */
    public FinancialHealthScore calculateScore(User user, String month) {
        // Retrieve all budgets for this user and month
        List<Budget> budgets = budgetRepository.findByUserAndMonth(user, month);

        double scoreValue;
        String grade;

        if (budgets.isEmpty()) {
            // No budgets — default to perfect score
            scoreValue = 100.0;
            grade = "A";
        } else {
            // Calculate average percentage of budget used across all categories
            double totalPercentage = budgets.stream()
                    .mapToDouble(budget -> {
                        if (budget.getMaxAmount() == 0) return 0;
                        return (budget.getSpentAmount() / budget.getMaxAmount()) * 100;
                    })
                    .sum();

            double averagePercentage = totalPercentage / budgets.size();

            // Apply score formula based on average budget usage
            if (averagePercentage <= 50) {
                // Excellent: 90-100 range, scaled within the 0-50% usage zone
                scoreValue = 100 - (averagePercentage / 50) * 10;
                grade = "A";
            } else if (averagePercentage <= 80) {
                // Good: 60-89 range, scaled within the 50-80% usage zone
                scoreValue = 89 - ((averagePercentage - 50) / 30) * 29;
                grade = "B";
            } else {
                // Needs improvement: 0-59 range, scaled within the 80-100%+ usage zone
                scoreValue = Math.max(0, 59 - ((averagePercentage - 80) / 20) * 59);
                grade = "C";
            }
        }

        // Find existing score for this month or create a new one
        Optional<FinancialHealthScore> existingScore = scoreRepository.findByUserAndMonth(user, month);

        FinancialHealthScore score;
        if (existingScore.isPresent()) {
            // Update the existing score
            score = existingScore.get();
            score.setValue(scoreValue);
            score.setGrade(grade);
            score.setDate(new Date());
        } else {
            // Create a new score record
            score = FinancialHealthScore.builder()
                    .value(scoreValue)
                    .grade(grade)
                    .month(month)
                    .date(new Date())
                    .user(user)
                    .build();
        }

        FinancialHealthScore savedScore = scoreRepository.save(score);
        log.info("Calculated score for user {} in {}: {} ({})", user.getEmail(), month, scoreValue, grade);
        return savedScore;
    }

    /**
     * Retrieve all financial health scores for the authenticated user, newest first.
     * Used to display the score evolution chart in the statistics page.
     *
     * @param userEmail the email of the authenticated user
     * @return a list of ScoreDTOs ordered by date descending
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    public List<ScoreDTO> getAllScores(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return scoreRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve the financial health score for the current month.
     * If no score exists yet, it will be calculated and saved first.
     *
     * @param userEmail the email of the authenticated user
     * @return a ScoreDTO for the current month, or null if no data is available
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    public ScoreDTO getCurrentMonthScore(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Determine the current month in "YYYY-MM" format
        String currentMonth = YearMonth.now().format(MONTH_FORMATTER);

        // Try to find existing score; if not found, calculate it now
        Optional<FinancialHealthScore> score = scoreRepository.findByUserAndMonth(user, currentMonth);
        if (score.isEmpty()) {
            FinancialHealthScore newScore = calculateScore(user, currentMonth);
            return mapToDTO(newScore);
        }

        return mapToDTO(score.get());
    }

    /**
     * Convert a FinancialHealthScore entity to a ScoreDTO for API responses.
     *
     * @param score the FinancialHealthScore entity to convert
     * @return a ScoreDTO with the entity's data
     */
    public ScoreDTO mapToDTO(FinancialHealthScore score) {
        return ScoreDTO.builder()
                .id(score.getId())
                .value(score.getValue())
                .grade(score.getGrade())
                .month(score.getMonth())
                .date(score.getDate())
                .build();
    }
}
