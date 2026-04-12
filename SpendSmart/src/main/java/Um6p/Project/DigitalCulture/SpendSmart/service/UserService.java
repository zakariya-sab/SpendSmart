package Um6p.Project.DigitalCulture.SpendSmart.service;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.UserDTO;
import Um6p.Project.DigitalCulture.SpendSmart.entities.User;
import Um6p.Project.DigitalCulture.SpendSmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("An account with email '" + dto.getEmail() + "' already exists");
        }

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("USER")
                .createdAt(new Date())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user: {}", savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    /** Password is excluded from the returned DTO. */
    public UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(null)
                .role(user.getRole())
                .build();
    }
}
