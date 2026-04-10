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

/**
 * REST controller for financial alert management.
 * All endpoints require a valid JWT token (protected by Spring Security).
 * Handles retrieval and read-status updates for budget alerts.
 * Base path: /api/alerts
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AlertRestController {

    /** Service containing alert business logic */
    private final AlertService alertService;

    /**
     * Get all unread alerts for the currently authenticated user.
     * Used to display the notification badge count and alert list.
     *
     * GET /api/alerts
     *
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of unread AlertDTOs
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AlertDTO>>> getUnreadAlerts(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AlertDTO> alerts = alertService.getUnreadAlerts(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("Unread alerts retrieved successfully", alerts));
    }

    /**
     * Get all alerts (both read and unread) for the currently authenticated user.
     * Used to display the full alert history page.
     *
     * GET /api/alerts/all
     *
     * @param authentication the Spring Security authentication object
     * @return 200 with a list of all AlertDTOs
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<AlertDTO>>> getAllAlerts(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AlertDTO> alerts = alertService.getAllAlerts(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("All alerts retrieved successfully", alerts));
    }

    /**
     * Mark a specific alert as read by its ID.
     * Removes the alert from the unread notification count.
     *
     * PUT /api/alerts/{id}/read
     *
     * @param id the ID of the alert to mark as read
     * @return 200 with the updated AlertDTO, or 400 if alert not found
     */
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

    /**
     * Mark all unread alerts as read for the currently authenticated user.
     * Clears the notification badge completely.
     *
     * PUT /api/alerts/read-all
     *
     * @param authentication the Spring Security authentication object
     * @return 200 on success
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponseDTO<Void>> markAllAsRead(Authentication authentication) {
        String userEmail = authentication.getName();
        alertService.markAllAsRead(userEmail);
        return ResponseEntity.ok(ApiResponseDTO.success("All alerts marked as read", null));
    }
}
