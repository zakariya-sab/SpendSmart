package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Alert;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUserAndIsReadFalse(User user);

    List<Alert> findByUser(User user);
}
