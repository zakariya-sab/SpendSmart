package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.AlertDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AlertRestController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AlertDTO>>> getUnreadAlerts(Authentication authentication) {
        List<AlertDTO> alerts = alertService.getUnreadAlerts(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("Unread alerts retrieved successfully", alerts));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<AlertDTO>>> getAllAlerts(Authentication authentication) {
        List<AlertDTO> alerts = alertService.getAllAlerts(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("All alerts retrieved successfully", alerts));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponseDTO<AlertDTO>> markAsRead(@PathVariable Long id) {
        try {
            AlertDTO updatedAlert = alertService.markAsRead(id);
            return ResponseEntity.ok(ApiResponseDTO.success("Alert marked as read", updatedAlert));
        } catch (RuntimeException e) {
            log.error("Failed to mark alert {} as read: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponseDTO<Void>> markAllAsRead(Authentication authentication) {
        alertService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok(ApiResponseDTO.success("All alerts marked as read", null));
    }
}
