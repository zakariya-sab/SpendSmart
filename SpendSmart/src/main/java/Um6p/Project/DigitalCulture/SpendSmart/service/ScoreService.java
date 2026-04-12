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

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreService {

    private final FInancialHealthScoreRepository scoreRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Score formula based on average % of budget used across all categories:
     *   ≤50%  → grade A, score 90-100
     *   ≤80%  → grade B, score 60-89
     *   >80%  → grade C, score 0-59
     * Defaults to 100/A when the user has no budgets.
     */
    public FinancialHealthScore calculateScore(User user, String month) {
        List<Budget> budgets = budgetRepository.findByUserAndMonth(user, month);

        double scoreValue;
        String grade;

        if (budgets.isEmpty()) {
            scoreValue = 100.0;
            grade = "A";
        } else {
            double totalPercentage = budgets.stream()
                    .mapToDouble(budget -> {
                        if (budget.getMaxAmount() == 0) return 0;
                        return (budget.getSpentAmount() / budget.getMaxAmount()) * 100;
                    })
                    .sum();

            double averagePercentage = totalPercentage / budgets.size();

            if (averagePercentage <= 50) {
                scoreValue = 100 - (averagePercentage / 50) * 10;
                grade = "A";
            } else if (averagePercentage <= 80) {
                scoreValue = 89 - ((averagePercentage - 50) / 30) * 29;
                grade = "B";
            } else {
                scoreValue = Math.max(0, 59 - ((averagePercentage - 80) / 20) * 59);
                grade = "C";
            }
        }

        Optional<FinancialHealthScore> existingScore = scoreRepository.findByUserAndMonth(user, month);

        FinancialHealthScore score;
        if (existingScore.isPresent()) {
            score = existingScore.get();
            score.setValue(scoreValue);
            score.setGrade(grade);
            score.setDate(new Date());
        } else {
            score = FinancialHealthScore.builder()
                    .value(scoreValue)
                    .grade(grade)
                    .month(month)
                    .date(new Date())
                    .user(user)
                    .build();
        }

        FinancialHealthScore savedScore = scoreRepository.save(score);
        log.info("Score for user {} in {}: {} ({})", user.getEmail(), month, scoreValue, grade);
        return savedScore;
    }

    public List<ScoreDTO> getAllScores(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return scoreRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ScoreDTO getCurrentMonthScore(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        String currentMonth = YearMonth.now().format(MONTH_FORMATTER);
        Optional<FinancialHealthScore> score = scoreRepository.findByUserAndMonth(user, currentMonth);

        if (score.isEmpty()) {
            return mapToDTO(calculateScore(user, currentMonth));
        }

        return mapToDTO(score.get());
    }

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
