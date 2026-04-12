package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.MLService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ml")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class MLRestController {

    private final MLService mlService;

    @GetMapping("/predict")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> predictNextMonth(Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("ML prediction requested for user: {}", userEmail);
        Map<String, Object> prediction = mlService.predictNextMonthSpending(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Prediction retrieved successfully", prediction));
    }
}
