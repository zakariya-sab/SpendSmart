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

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ExpenseRestController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ExpenseDTO>>> getAllExpenses(Authentication authentication) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByUser(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("Expenses retrieved successfully", expenses));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ExpenseDTO>> addExpense(
            @Valid @RequestBody ExpenseDTO expenseDTO,
            Authentication authentication
    ) {
        try {
            ExpenseDTO created = expenseService.addExpense(expenseDTO, authentication.getName());
            return ResponseEntity.ok(ApiResponseDTO.success("Expense added successfully", created));
        } catch (RuntimeException e) {
            log.error("Failed to add expense: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

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

    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponseDTO<List<ExpenseDTO>>> getExpensesByMonth(
            @PathVariable String month,
            Authentication authentication
    ) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByMonth(authentication.getName(), month);
        return ResponseEntity.ok(ApiResponseDTO.success("Expenses for " + month + " retrieved successfully", expenses));
    }
}
