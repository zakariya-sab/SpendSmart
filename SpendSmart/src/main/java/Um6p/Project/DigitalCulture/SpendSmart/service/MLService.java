package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Expense;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.ExpenseRepository;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MLService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final RestTemplate restTemplate;

    @Value("${ml.api.url}")
    private String mlApiUrl;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /** Aggregates the last 3 months of expenses into monthly totals and sends them to the Flask API. */
    public Map<String, Object> predictNextMonthSpending(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        List<Map<String, Object>> monthlyExpenses = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int monthsAgo = 3; monthsAgo >= 1; monthsAgo--) {
            YearMonth targetMonth = currentMonth.minusMonths(monthsAgo);

            Date startDate = Date.from(targetMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(targetMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
            double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", targetMonth.format(MONTH_FORMATTER));
            monthData.put("amount", totalAmount);
            monthlyExpenses.add(monthData);
        }

        return callFlaskAPI(monthlyExpenses);
    }

    /** Returns a fallback response if the Flask API is unreachable. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> callFlaskAPI(List<Map<String, Object>> monthlyExpenses) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("expenses", monthlyExpenses);

            log.info("Calling Flask ML API at {} with {} months of data", mlApiUrl, monthlyExpenses.size());

            Map<String, Object> response = restTemplate.postForObject(
                    mlApiUrl,
                    requestBody,
                    Map.class
            );

            if (response != null) {
                log.info("Received prediction: {}", response.get("predicted_amount"));
                return response;
            }
        } catch (Exception e) {
            log.warn("Could not reach Flask ML API: {}. Returning fallback.", e.getMessage());
        }

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("predicted_amount", 0.0);
        fallback.put("trend", "unavailable");
        fallback.put("message", "ML service is currently unavailable");
        return fallback;
    }
}
