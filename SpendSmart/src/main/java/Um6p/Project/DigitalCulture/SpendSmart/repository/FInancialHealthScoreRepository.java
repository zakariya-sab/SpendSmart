package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.FinancialHealthScore;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FInancialHealthScoreRepository extends JpaRepository<FinancialHealthScore, Long> {

    Optional<FinancialHealthScore> findByUserAndMonth(User user, String month);

    List<FinancialHealthScore> findByUserOrderByDateDesc(User user);
}
