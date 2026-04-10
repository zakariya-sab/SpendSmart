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
import java.util.stream.Collectors;

/**
 * Service class for machine learning spending prediction.
 * Sends the user's last 3 months of expense history to the Python Flask API
 * and returns the predicted spending amount for next month.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MLService {

    /** Repository to look up users by email */
    private final UserRepository userRepository;

    /** Repository to look up expenses by user and date range */
    private final ExpenseRepository expenseRepository;

    /** URL of the Python Flask ML prediction API */
    @Value("${ml.api.url}")
    private String mlApiUrl;

    /** RestTemplate for making HTTP requests to the Flask API */
    private final RestTemplate restTemplate;

    /** Formatter for the "YYYY-MM" month string format */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Predict the next month's total spending for the authenticated user.
     * Aggregates the last 3 months of expenses into monthly totals,
     * sends them to the Flask API, and returns the prediction result.
     *
     * @param userEmail the email of the authenticated user
     * @return a Map containing "predicted_amount" (Double) and "trend" (String: "increasing"/"decreasing"/"stable")
     * @throws UsernameNotFoundException if the user is not found
     */
    public Map<String, Object> predictNextMonthSpending(String userEmail) {
        // Look up the authenticated user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // Build monthly totals for the last 3 months
        List<Map<String, Object>> monthlyExpenses = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int monthsAgo = 3; monthsAgo >= 1; monthsAgo--) {
            YearMonth targetMonth = currentMonth.minusMonths(monthsAgo);

            // Calculate date range for this month
            Date startDate = Date.from(targetMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(targetMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

            // Get all expenses for this month and sum them
            List<Expense> monthExpenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
            double totalAmount = monthExpenses.stream().mapToDouble(Expense::getAmount).sum();

            // Build the data point for this month
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", targetMonth.format(MONTH_FORMATTER));
            monthData.put("amount", totalAmount);
            monthlyExpenses.add(monthData);
        }

        // Send data to Flask API and return prediction
        return callFlaskAPI(monthlyExpenses);
    }

    /**
     * Call the Python Flask ML API with the expense history data.
     * Sends a POST request and parses the predicted spending response.
     * Returns a fallback response if the Flask API is unavailable.
     *
     * @param monthlyExpenses a list of {month, amount} maps representing monthly spending totals
     * @return a Map containing the prediction result from the Flask API,
     *         or a fallback with predicted_amount=0 if the API is unreachable
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> callFlaskAPI(List<Map<String, Object>> monthlyExpenses) {
        try {
            // Build the request payload in the format the Flask API expects
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("expenses", monthlyExpenses);

            log.info("Calling Flask ML API at {} with {} months of data", mlApiUrl, monthlyExpenses.size());

            // Send POST request to the Flask API and get the response
            Map<String, Object> response = restTemplate.postForObject(
                    mlApiUrl,
                    requestBody,
                    Map.class
            );

            if (response != null) {
                log.info("Received prediction from Flask API: {}", response.get("predicted_amount"));
                return response;
            }
        } catch (Exception e) {
            // Flask API unavailable — log warning and return a fallback response
            log.warn("Could not reach Flask ML API: {}. Returning fallback prediction.", e.getMessage());
        }

        // Return a fallback response when the ML API is unavailable
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("predicted_amount", 0.0);
        fallback.put("trend", "unavailable");
        fallback.put("message", "ML service is currently unavailable");
        return fallback;
    }
}
