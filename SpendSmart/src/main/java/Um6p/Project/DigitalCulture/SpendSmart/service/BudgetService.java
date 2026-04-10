package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.BudgetDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Budget;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.BudgetRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.CategoryRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing monthly spending budgets.
 * Handles budget creation, validation, and retrieval with remaining/percentage calculations.
 * Enforces one budget per user per category per month.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    /** Repository for budget database operations */
    private final BudgetRepository budgetRepository;

    /** Repository to look up users by email */
    private final UserRepository userRepository;

    /** Repository to look up categories by ID */
    private final CategoryRepository categoryRepository;

    /**
     * Create a new monthly budget for a user and category.
     * Validates that no budget already exists for the same user, category, and month.
     *
     * @param dto       the budget data (maxAmount, month, categoryId)
     * @param userEmail the email of the authenticated user creating the budget
     * @return the saved budget as a BudgetDTO with calculated remaining and percentage fields
     * @throws UsernameNotFoundException if the user is not found
     * @throws RuntimeException          if the category is not found or a budget already exists for this combination
     */
    public BudgetDTO createBudget(BudgetDTO dto, String userEmail) {
        // Look up the authenticated user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Look up the specified category
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // Prevent duplicate budgets — one per user/category/month combination
        if (budgetRepository.findByUserAndCategoryAndMonth(user, category, dto.getMonth()).isPresent()) {
            throw new RuntimeException(
                    "Budget already exists for this category this month: "
                            + category.getName() + " / " + dto.getMonth()
            );
        }

        // Build and save the new budget with zero initial spending
        Budget budget = Budget.builder()
                .maxAmount(dto.getMaxAmount())
                .spentAmount(0.0)
                .month(dto.getMonth())
                .user(user)
                .category(category)
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        log.info("Created budget for user {} in {} for category {}", userEmail, dto.getMonth(), category.getName());
        return mapToDTO(savedBudget);
    }

    /**
     * Retrieve all budgets for the authenticated user in a specific month.
     * Each budget includes the calculated remaining amount and percentage used.
     *
     * @param userEmail the email of the authenticated user
     * @param month     the month to filter by in "YYYY-MM" format (e.g. "2026-04")
     * @return a list of BudgetDTOs for the specified month
     * @throws UsernameNotFoundException if the user is not found
     */
    public List<BudgetDTO> getBudgetsByMonth(String userEmail, String month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return budgetRepository.findByUserAndMonth(user, month)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculate the remaining amount for a budget.
     * Remaining = maxAmount - spentAmount.
     * Returns 0 if spentAmount exceeds maxAmount (budget is exceeded).
     *
     * @param budget the budget to calculate remaining amount for
     * @return the remaining amount, never negative
     */
    public double getRemainingAmount(Budget budget) {
        return Math.max(0, budget.getMaxAmount() - budget.getSpentAmount());
    }

    /**
     * Convert a Budget entity to a BudgetDTO with calculated financial fields.
     * Adds remaining amount and percentage used for display in the UI.
     *
     * @param budget the Budget entity to convert
     * @return a BudgetDTO with all fields populated
     */
    public BudgetDTO mapToDTO(Budget budget) {
        // Calculate how much of the budget has been used as a percentage
        double percentageUsed = budget.getMaxAmount() > 0
                ? (budget.getSpentAmount() / budget.getMaxAmount()) * 100
                : 0;

        return BudgetDTO.builder()
                .id(budget.getId())
                .maxAmount(budget.getMaxAmount())
                .spentAmount(budget.getSpentAmount())
                .remainingAmount(getRemainingAmount(budget))
                .percentageUsed(Math.min(100, percentageUsed))
                .month(budget.getMonth())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryColor(budget.getCategory().getColor())
                .build();
    }
}
