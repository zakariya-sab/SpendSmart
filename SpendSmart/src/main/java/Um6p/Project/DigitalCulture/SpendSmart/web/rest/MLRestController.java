package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.MLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for Machine Learning spending prediction.
 * All endpoints require a valid JWT token (protected by Spring Security).
 * Proxies requests to the Python Flask ML API for next month predictions.
 * Base path: /api/ml
 */
@RestController
@RequestMapping("/api/ml")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class MLRestController {

    /** Service that communicates with the Python Flask ML API */
    private final MLService mlService;

    /**
     * Get the predicted spending amount for next month.
     * Uses the user's last 3 months of expense history to generate the prediction.
     * Returns a fallback response if the Flask ML API is unavailable.
     *
     * GET /api/ml/predict
     *
     * @param authentication the Spring Security authentication object
     * @return 200 with prediction data: { predicted_amount: double, trend: String }
     */
    @GetMapping("/predict")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> predictNextMonth(Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("ML prediction requested for user: {}", userEmail);
        Map<String, Object> prediction = mlService.predictNextMonthSpending(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Prediction retrieved successfully", prediction));
    }
}
