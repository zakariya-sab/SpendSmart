package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.ScoreDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScoreRestController {

    private final ScoreService scoreService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ScoreDTO>>> getAllScores(Authentication authentication) {
        List<ScoreDTO> scores = scoreService.getAllScores(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("Scores retrieved successfully", scores));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponseDTO<ScoreDTO>> getCurrentScore(Authentication authentication) {
        ScoreDTO currentScore = scoreService.getCurrentMonthScore(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("Current month score retrieved", currentScore));
    }
}
