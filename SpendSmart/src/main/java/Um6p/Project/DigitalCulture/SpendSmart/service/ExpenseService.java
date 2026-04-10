package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ExpenseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.*;
import Um6p.Project.DigitalCulture.SpendSmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user expenses.
 * Handles expense creation, deletion, retrieval, and filtering by month.
 * When an expense is added, it automatically:
 *   - Updates the budget's spentAmount for the corresponding category/month
 *   - Creates a WARNING alert if budget usage reaches 80%
 *   - Creates an EXCEEDED alert if budget usage reaches 100%
 *   - Recalculates the user's financial health score for the month
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    /** Repository for expense database operations */
    private final ExpenseRepository expenseRepository;

    /** Repository for budget database operations */
    private final BudgetRepository budgetRepository;

    /** Repository for alert database operations */
    private final AlertRepository alertRepository;

    /** Repository to look up users by email */
    private final UserRepository userRepository;

    /** Repository to look up categories by ID */
    private final CategoryRepository categoryRepository;

    /** Service to recalculate financial health scores after expense changes */
    private final ScoreService scoreService;

    /** Formatter for the "YYYY-MM" month string format */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Add a new expense for the authenticated user.
     * After saving the expense:
     *   1. Finds the matching budget (same user, category, and current month)
     *   2. Updates the budget's spentAmount
     *   3. Generates WARNING alert if usage >= 80% and < 100%
     *   4. Generates EXCEEDED alert if usage >= 100%
     *   5. Recalculates the user's financial health score for the month
     *
     * @param dto       the expense data (amount, date, description, categoryId)
     * @param userEmail the email of the authenticated user
     * @return the saved expense as an ExpenseDTO
     * @throws UsernameNotFoundException if the user is not found
     * @throws RuntimeException          if the category is not found
     */
    @Transactional
    public ExpenseDTO addExpense(ExpenseDTO dto, String userEmail) {
        // Look up the authenticated user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Look up the specified category
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // Build and save the new expense
        Expense expense = Expense.builder()
                .amount(dto.getAmount())
                .date(dto.getDate())
                .description(dto.getDescription())
                .user(user)
                .category(category)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Added expense of {} for user {} in category {}", dto.getAmount(), userEmail, category.getName());

        // Determine the month of this expense in "YYYY-MM" format
        String expenseMonth = getMonthString(dto.getDate());

        // Find the budget for this user, category, and month
        Optional<Budget> budgetOptional = budgetRepository.findByUserAndCategoryAndMonth(
                user, category, expenseMonth
        );

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();

            // Update the budget's spent amount
            budget.setSpentAmount(budget.getSpentAmount() + dto.getAmount());
            budgetRepository.save(budget);

            // Calculate what percentage of the budget has been used
            double percentageUsed = (budget.getSpentAmount() / budget.getMaxAmount()) * 100;

            // Generate alerts based on spending thresholds
            if (percentageUsed >= 100) {
                // Budget completely exceeded — create EXCEEDED alert if not already present
                createAlertIfNotExists(user, budget, "EXCEEDED",
                        String.format("You have EXCEEDED your %s budget! (%.1f%% used)",
                                category.getName(), percentageUsed));
            } else if (percentageUsed >= 80) {
                // Budget approaching limit — create WARNING alert if not already present
                createAlertIfNotExists(user, budget, "WARNING",
                        String.format("Warning: You have used %.1f%% of your %s budget",
                                percentageUsed, category.getName()));
            }
        }

        // Recalculate the financial health score for this month
        scoreService.calculateScore(user, expenseMonth);

        return mapToDTO(savedExpense);
    }

    /**
     * Retrieve all expenses for the authenticated user across all time.
     *
     * @param userEmail the email of the authenticated user
     * @return a list of all ExpenseDTOs for this user
     * @throws UsernameNotFoundException if the user is not found
     */
    public List<ExpenseDTO> getExpensesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return expenseRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete an expense by its ID and recalculate the affected budget and score.
     * After deletion, the budget's spentAmount is reduced by the expense amount,
     * and the financial health score is recalculated.
     *
     * @param expenseId the ID of the expense to delete
     * @throws RuntimeException if no expense with the given ID exists
     */
    @Transactional
    public void deleteExpense(Long expenseId) {
        // Find the expense or throw if not found
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));

        // Determine the month of the deleted expense
        String expenseMonth = getMonthString(expense.getDate());
        User user = expense.getUser();
        Category category = expense.getCategory();

        // Update the related budget's spent amount (subtract the deleted expense)
        Optional<Budget> budgetOptional = budgetRepository.findByUserAndCategoryAndMonth(
                user, category, expenseMonth
        );

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            double newSpentAmount = Math.max(0, budget.getSpentAmount() - expense.getAmount());
            budget.setSpentAmount(newSpentAmount);
            budgetRepository.save(budget);
        }

        // Delete the expense
        expenseRepository.delete(expense);
        log.info("Deleted expense {} for user {}", expenseId, user.getEmail());

        // Recalculate the financial health score for the affected month
        scoreService.calculateScore(user, expenseMonth);
    }

    /**
     * Retrieve all expenses for the authenticated user filtered by month.
     * The month parameter should be in "YYYY-MM" format (e.g. "2026-04").
     *
     * @param userEmail the email of the authenticated user
     * @param month     the month to filter by in "YYYY-MM" format
     * @return a list of ExpenseDTOs for the specified month
     * @throws UsernameNotFoundException if the user is not found
     */
    public List<ExpenseDTO> getExpensesByMonth(String userEmail, String month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Calculate the first and last day of the specified month
        YearMonth yearMonth = YearMonth.parse(month, MONTH_FORMATTER);
        Date startDate = Date.from(yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a budget alert if an alert of the same type for the same budget does not already exist.
     * Prevents duplicate alerts from being created when multiple expenses cross the same threshold.
     *
     * @param user    the user to create the alert for
     * @param budget  the budget that triggered the alert
     * @param type    the alert type: "WARNING" or "EXCEEDED"
     * @param message the human-readable alert message
     */
    private void createAlertIfNotExists(User user, Budget budget, String type, String message) {
        // Check if an unread alert of this type already exists for this budget
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

    /**
     * Convert a Date to a "YYYY-MM" formatted month string.
     *
     * @param date the date to extract the month from
     * @return the month in "YYYY-MM" format
     */
    private String getMonthString(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(MONTH_FORMATTER);
    }

    /**
     * Convert an Expense entity to an ExpenseDTO for API responses.
     *
     * @param expense the Expense entity to convert
     * @return an ExpenseDTO with all fields populated
     */
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
