package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.BudgetDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for budget management operations.
 * All endpoints require a valid JWT token (protected by Spring Security).
 * Handles budget creation and retrieval for specific months.
 * Base path: /api/budgets
 */
@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BudgetRestController {

    /** Service containing budget business logic */
    private final BudgetService budgetService;

    /**
     * Get all budgets for the currently authenticated user in a specific month.
     * Each budget includes remaining amount and percentage used.
     *
     * GET /api/budgets/month/{month}
     *
     * @param month          the month in "YYYY-MM" format (e.g. "2026-04")
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of BudgetDTOs for the specified month
     */
    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponseDTO<List<BudgetDTO>>> getBudgetsByMonth(
            @PathVariable String month,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        List<BudgetDTO> budgets = budgetService.getBudgetsByMonth(userEmail, month);
        return ResponseEntity.ok(ApiResponseDTO.success("Budgets for " + month + " retrieved successfully", budgets));
    }

    /**
     * Create a new monthly budget for the currently authenticated user.
     * Only one budget per category per month is allowed.
     *
     * POST /api/budgets
     *
     * @param budgetDTO      the budget data (maxAmount, month, categoryId)
     * @param authentication the Spring Security authentication object
     * @return 200 with the created BudgetDTO, or 400 if budget already exists or validation fails
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<BudgetDTO>> createBudget(
            @Valid @RequestBody BudgetDTO budgetDTO,
            Authentication authentication
    ) {
        try {
            String userEmail = authentication.getName();
            BudgetDTO created = budgetService.createBudget(budgetDTO, userEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Budget created successfully", created));
        } catch (RuntimeException e) {
            log.error("Failed to create budget: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Get all budgets with remaining amounts for the authenticated user in a specific month.
     * Alias for getBudgetsByMonth that emphasizes the remaining amount field in BudgetDTO.
     *
     * GET /api/budgets/remaining/{month}
     *
     * @param month          the month in "YYYY-MM" format (e.g. "2026-04")
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of BudgetDTOs including remaining amounts
     */
    @GetMapping("/remaining/{month}")
    public ResponseEntity<ApiResponseDTO<List<BudgetDTO>>> getRemainingBudgets(
            @PathVariable String month,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        List<BudgetDTO> budgets = budgetService.getBudgetsByMonth(userEmail, month);
        return ResponseEntity.ok(ApiResponseDTO.success("Remaining budgets for " + month, budgets));
    }
}
