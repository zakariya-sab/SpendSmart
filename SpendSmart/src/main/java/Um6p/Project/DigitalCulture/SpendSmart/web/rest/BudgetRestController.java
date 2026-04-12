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

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BudgetRestController {

    private final BudgetService budgetService;

    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponseDTO<List<BudgetDTO>>> getBudgetsByMonth(
            @PathVariable String month,
            Authentication authentication
    ) {
        List<BudgetDTO> budgets = budgetService.getBudgetsByMonth(authentication.getName(), month);
        return ResponseEntity.ok(ApiResponseDTO.success("Budgets for " + month + " retrieved successfully", budgets));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<BudgetDTO>> createBudget(
            @Valid @RequestBody BudgetDTO budgetDTO,
            Authentication authentication
    ) {
        try {
            BudgetDTO created = budgetService.createBudget(budgetDTO, authentication.getName());
            return ResponseEntity.ok(ApiResponseDTO.success("Budget created successfully", created));
        } catch (RuntimeException e) {
            log.error("Failed to create budget: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/remaining/{month}")
    public ResponseEntity<ApiResponseDTO<List<BudgetDTO>>> getRemainingBudgets(
            @PathVariable String month,
            Authentication authentication
    ) {
        List<BudgetDTO> budgets = budgetService.getBudgetsByMonth(authentication.getName(), month);
        return ResponseEntity.ok(ApiResponseDTO.success("Remaining budgets for " + month, budgets));
    }
}
