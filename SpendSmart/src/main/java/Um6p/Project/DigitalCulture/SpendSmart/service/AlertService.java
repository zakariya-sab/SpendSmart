package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.AlertDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.Alert;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.AlertRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing budget alerts.
 * Handles retrieval and read-status management of financial alerts.
 * Alerts are created automatically by ExpenseService when budget thresholds are crossed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    /** Repository for alert database operations */
    private final AlertRepository alertRepository;

    /** Repository to look up users by email */
    private final UserRepository userRepository;

    /**
     * Retrieve all unread alerts for the currently logged-in user.
     * Used to display the notification count badge and the alerts list.
     *
     * @param userEmail the email of the authenticated user
     * @return a list of unread AlertDTO objects for this user
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    public List<AlertDTO> getUnreadAlerts(String userEmail) {
        // Find the user by email or throw if not found
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Fetch only unread alerts and convert to DTOs
        return alertRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all alerts (both read and unread) for the currently logged-in user.
     * Used to display the full alert history in the alerts page.
     *
     * @param userEmail the email of the authenticated user
     * @return a list of all AlertDTO objects for this user
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    public List<AlertDTO> getAllAlerts(String userEmail) {
        // Find the user by email or throw if not found
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Fetch all alerts and convert to DTOs
        return alertRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark a specific alert as read by its ID.
     * Sets the isRead flag to true so the alert is no longer shown as a notification.
     *
     * @param alertId the ID of the alert to mark as read
     * @return the updated AlertDTO with isRead = true
     * @throws RuntimeException if no alert with the given ID exists
     */
    public AlertDTO markAsRead(Long alertId) {
        // Find the alert or throw if not found
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));

        // Mark as read and save
        alert.setRead(true);
        Alert savedAlert = alertRepository.save(alert);
        log.info("Marked alert {} as read", alertId);
        return mapToDTO(savedAlert);
    }

    /**
     * Mark all alerts as read for the currently logged-in user.
     * Called when the user clicks "Mark all as read" in the alerts page.
     *
     * @param userEmail the email of the authenticated user
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    public void markAllAsRead(String userEmail) {
        // Find the user by email or throw if not found
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Find all unread alerts for this user
        List<Alert> unreadAlerts = alertRepository.findByUserAndIsReadFalse(user);

        // Mark each alert as read and save
        unreadAlerts.forEach(alert -> alert.setRead(true));
        alertRepository.saveAll(unreadAlerts);
        log.info("Marked {} alerts as read for user: {}", unreadAlerts.size(), userEmail);
    }

    /**
     * Convert an Alert entity to an AlertDTO for API responses.
     *
     * @param alert the Alert entity to convert
     * @return an AlertDTO with the entity's data
     */
    public AlertDTO mapToDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .message(alert.getMessage())
                .date(alert.getDate())
                .isRead(alert.isRead())
                .type(alert.getType())
                // Include category name and budget month for context in the UI
                .categoryName(alert.getBudget() != null && alert.getBudget().getCategory() != null
                        ? alert.getBudget().getCategory().getName() : null)
                .budgetMonth(alert.getBudget() != null ? alert.getBudget().getMonth() : null)
                .build();
    }
}
