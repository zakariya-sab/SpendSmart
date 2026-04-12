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

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetDTO createBudget(BudgetDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        if (budgetRepository.findByUserAndCategoryAndMonth(user, category, dto.getMonth()).isPresent()) {
            throw new RuntimeException(
                    "Budget already exists for this category this month: "
                            + category.getName() + " / " + dto.getMonth()
            );
        }

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

    public List<BudgetDTO> getBudgetsByMonth(String userEmail, String month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return budgetRepository.findByUserAndMonth(user, month)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public double getRemainingAmount(Budget budget) {
        return Math.max(0, budget.getMaxAmount() - budget.getSpentAmount());
    }

    public BudgetDTO mapToDTO(Budget budget) {
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
