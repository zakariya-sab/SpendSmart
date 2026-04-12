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

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpendSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpendSmartApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /** Seeds default categories and admin user (admin@spendsmart.com / admin123) on first startup. */
    @Bean
    public CommandLineRunner initData(
            CategoryService categoryService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            categoryService.initDefaultCategories();

            if (!userRepository.existsByEmail("admin@spendsmart.com")) {
                User adminUser = User.builder()
                        .firstName("Admin")
                        .lastName("SpendSmart")
                        .email("admin@spendsmart.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ADMIN")
                        .createdAt(new Date())
                        .build();

                userRepository.save(adminUser);
                log.info("Default admin user created: admin@spendsmart.com");
            }
        };
    }
}
