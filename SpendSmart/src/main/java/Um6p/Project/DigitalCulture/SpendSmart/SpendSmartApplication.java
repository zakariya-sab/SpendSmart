package Um6p.Project.DigitalCulture.SpendSmart;

import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import Um6p.Project.DigitalCulture.SpendSmart.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Main entry point for the SpendSmart Spring Boot application.
 * Initializes the application with default data on startup:
 *   - Default spending categories (Food, Transport, Health, etc.)
 *   - A default admin user account
 *
 * Project: SpendSmart — Personal Finance Analyzer and Predictor
 * Authors: Zakariya Sabri & Ahmed Khalil EL ATRI
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpendSmartApplication {

    /**
     * Application main method — starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SpendSmartApplication.class, args);
        log.info("SpendSmart application started successfully on port 8080");
    }

    /**
     * Provides a RestTemplate bean used by MLService to call the Flask API.
     * RestTemplate is the Spring HTTP client for making synchronous REST calls.
     *
     * @return a new RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * CommandLineRunner that initializes default data when the application starts.
     * Runs after the application context is fully loaded.
     * Creates:
     *   1. Default categories if the categories table is empty
     *   2. A default admin user (admin@spendsmart.com / admin123) if no admin exists
     *
     * @param categoryService service for category initialization
     * @param userRepository  repository to check for existing users
     * @param passwordEncoder encoder to BCrypt-hash the admin password
     * @return a CommandLineRunner lambda that performs the initialization
     */
    @Bean
    public CommandLineRunner initData(
            CategoryService categoryService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            log.info("Starting SpendSmart data initialization...");

            // Step 1: Initialize default categories if none exist
            categoryService.initDefaultCategories();

            // Step 2: Create the default admin user if no admin account exists
            if (!userRepository.existsByEmail("admin@spendsmart.com")) {
                User adminUser = User.builder()
                        .firstName("Admin")
                        .lastName("SpendSmart")
                        .email("admin@spendsmart.com")
                        // BCrypt-encrypt the default admin password
                        .password(passwordEncoder.encode("admin123"))
                        .role("ADMIN")
                        .createdAt(new Date())
                        .build();

                userRepository.save(adminUser);
                log.info("Default admin user created: admin@spendsmart.com");
            }

            log.info("SpendSmart data initialization complete.");
        };
    }
}
