package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Alert;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Alert entity database operations.
 * Provides methods to fetch unread and all alerts for a specific user.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    /**
     * Find all unread alerts for a specific user.
     * Used to show the count of pending notifications and the alert list.
     *
     * @param user the user whose unread alerts to retrieve
     * @return list of alerts that have not been read yet (isRead = false)
     */
    List<Alert> findByUserAndIsReadFalse(User user);

    /**
     * Find all alerts for a specific user (both read and unread).
     * Used to show the full alert history in the alerts page.
     *
     * @param user the user whose alerts to retrieve
     * @return list of all alerts belonging to the user
     */
    List<Alert> findByUser(User user);
}
