package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.ScoreDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for financial health score operations.
 * All endpoints require a valid JWT token (protected by Spring Security).
 * Provides access to historical scores and the current month's score.
 * Base path: /api/scores
 */
@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScoreRestController {

    /** Service containing financial health score business logic */
    private final ScoreService scoreService;

    /**
     * Get all financial health scores for the currently authenticated user.
     * Returns scores ordered from newest to oldest for chart display.
     *
     * GET /api/scores
     *
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of all ScoreDTOs for this user
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ScoreDTO>>> getAllScores(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ScoreDTO> scores = scoreService.getAllScores(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Scores retrieved successfully", scores));
    }

    /**
     * Get the financial health score for the current month.
     * If no score has been calculated yet this month, it will be calculated on the fly.
     *
     * GET /api/scores/current
     *
     * @param authentication the Spring Security authentication object
     * @return 200 with the current month's ScoreDTO
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponseDTO<ScoreDTO>> getCurrentScore(Authentication authentication) {
        String userEmail = authentication.getName();
        ScoreDTO currentScore = scoreService.getCurrentMonthScore(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Current month score retrieved", currentScore));
    }
}
