package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ExpenseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.*;
import Um6p.Project.DigitalCulture.SpendSmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ScoreService scoreService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Saves the expense, then:
     *  1. Updates the matching budget's spentAmount
     *  2. Creates a WARNING alert at ≥80% usage, EXCEEDED at ≥100%
     *  3. Recalculates the financial health score for the month
     */
    @Transactional
    public ExpenseDTO addExpense(ExpenseDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        Expense expense = Expense.builder()
                .amount(dto.getAmount())
                .date(dto.getDate())
                .description(dto.getDescription())
                .user(user)
                .category(category)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Added expense of {} for user {} in category {}", dto.getAmount(), userEmail, category.getName());

        String expenseMonth = getMonthString(dto.getDate());

        Optional<Budget> budgetOptional = budgetRepository.findByUserAndCategoryAndMonth(
                user, category, expenseMonth
        );

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            budget.setSpentAmount(budget.getSpentAmount() + dto.getAmount());
            budgetRepository.save(budget);

            double percentageUsed = (budget.getSpentAmount() / budget.getMaxAmount()) * 100;

            if (percentageUsed >= 100) {
                createAlertIfNotExists(user, budget, "EXCEEDED",
                        String.format("You have EXCEEDED your %s budget! (%.1f%% used)",
                                category.getName(), percentageUsed));
            } else if (percentageUsed >= 80) {
                createAlertIfNotExists(user, budget, "WARNING",
                        String.format("Warning: You have used %.1f%% of your %s budget",
                                percentageUsed, category.getName()));
            }
        }

        scoreService.calculateScore(user, expenseMonth);
        return mapToDTO(savedExpense);
    }

    public List<ExpenseDTO> getExpensesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return expenseRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));

        String expenseMonth = getMonthString(expense.getDate());
        User user = expense.getUser();
        Category category = expense.getCategory();

        Optional<Budget> budgetOptional = budgetRepository.findByUserAndCategoryAndMonth(
                user, category, expenseMonth
        );

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            budget.setSpentAmount(Math.max(0, budget.getSpentAmount() - expense.getAmount()));
            budgetRepository.save(budget);
        }

        expenseRepository.delete(expense);
        log.info("Deleted expense {} for user {}", expenseId, user.getEmail());

        scoreService.calculateScore(user, expenseMonth);
    }

    public List<ExpenseDTO> getExpensesByMonth(String userEmail, String month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        YearMonth yearMonth = YearMonth.parse(month, MONTH_FORMATTER);
        Date startDate = Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Avoids duplicate alerts: skips creation if an unread alert of the same type already exists for this budget. */
    private void createAlertIfNotExists(User user, Budget budget, String type, String message) {
        boolean alertExists = alertRepository.findByUserAndIsReadFalse(user)
                .stream()
                .anyMatch(a -> a.getBudget().getId().equals(budget.getId()) && type.equals(a.getType()));

        if (!alertExists) {
            Alert alert = Alert.builder()
                    .message(message)
                    .date(new Date())
                    .isRead(false)
                    .type(type)
                    .user(user)
                    .budget(budget)
                    .build();
            alertRepository.save(alert);
            log.info("Created {} alert for user {} budget {}", type, user.getEmail(), budget.getId());
        }
    }

    private String getMonthString(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(MONTH_FORMATTER);
    }

    public ExpenseDTO mapToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .description(expense.getDescription())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .categoryColor(expense.getCategory().getColor())
                .build();
    }
}
