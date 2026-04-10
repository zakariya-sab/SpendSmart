package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.ExpenseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for expense management operations.
 * All endpoints require a valid JWT token (protected by Spring Security).
 * The authenticated user's email is extracted from the JWT for each request.
 * Base path: /api/expenses
 */
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ExpenseRestController {

    /** Service containing expense business logic */
    private final ExpenseService expenseService;

    /**
     * Get all expenses for the currently authenticated user.
     * Returns all expenses across all months and categories.
     *
     * GET /api/expenses
     *
     * @param authentication the Spring Security authentication object (contains logged-in user email)
     * @return 200 with a list of all ExpenseDTOs for this user
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ExpenseDTO>>> getAllExpenses(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseDTO> expenses = expenseService.getExpensesByUser(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Expenses retrieved successfully", expenses));
    }

    /**
     * Add a new expense for the currently authenticated user.
     * After creation, automatically updates the related budget and recalculates the health score.
     *
     * POST /api/expenses
     *
     * @param expenseDTO     the expense data to create (amount, date, description, categoryId)
     * @param authentication the Spring Security authentication object
     * @return 200 with the created ExpenseDTO, or 400 if validation fails
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ExpenseDTO>> addExpense(
            @Valid @RequestBody ExpenseDTO expenseDTO,
            Authentication authentication
    ) {
        try {
            String userEmail = authentication.getName();
            ExpenseDTO created = expenseService.addExpense(expenseDTO, userEmail);
            return ResponseEntity.ok(ApiResponseDTO.success("Expense added successfully", created));
        } catch (RuntimeException e) {
            log.error("Failed to add expense: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Delete an expense by its ID.
     * Also updates the related budget's spent amount and recalculates the health score.
     *
     * DELETE /api/expenses/{id}
     *
     * @param id the ID of the expense to delete
     * @return 200 on success, or 400 if the expense is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Expense deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete expense {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Get all expenses for the currently authenticated user filtered by month.
     * The month parameter must be in "YYYY-MM" format (e.g. "2026-04").
     *
     * GET /api/expenses/month/{month}
     *
     * @param month          the month to filter by (e.g. "2026-04")
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of ExpenseDTOs for the specified month
     */
    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponseDTO<List<ExpenseDTO>>> getExpensesByMonth(
            @PathVariable String month,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        List<ExpenseDTO> expenses = expenseService.getExpensesByMonth(userEmail, month);
        return ResponseEntity.ok(ApiResponseDTO.success("Expenses for " + month + " retrieved successfully", expenses));
    }
}
