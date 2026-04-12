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

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public List<AlertDTO> getUnreadAlerts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return alertRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getAllAlerts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        return alertRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AlertDTO markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found with ID: " + alertId));

        alert.setRead(true);
        Alert savedAlert = alertRepository.save(alert);
        log.info("Marked alert {} as read", alertId);
        return mapToDTO(savedAlert);
    }

    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        List<Alert> unreadAlerts = alertRepository.findByUserAndIsReadFalse(user);
        unreadAlerts.forEach(alert -> alert.setRead(true));
        alertRepository.saveAll(unreadAlerts);
        log.info("Marked {} alerts as read for user: {}", unreadAlerts.size(), userEmail);
    }

    public AlertDTO mapToDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .message(alert.getMessage())
                .date(alert.getDate())
                .isRead(alert.isRead())
                .type(alert.getType())
                .categoryName(alert.getBudget() != null && alert.getBudget().getCategory() != null
                        ? alert.getBudget().getCategory().getName() : null)
                .budgetMonth(alert.getBudget() != null ? alert.getBudget().getMonth() : null)
                .build();
    }
}
