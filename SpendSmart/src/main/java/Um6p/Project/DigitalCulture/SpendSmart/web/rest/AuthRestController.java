package Um6p.Project.DigitalCulture.SpendSmart.web.rest;

import Um6p.Project.DigitalCulture.SpendSmart.dtos.ApiResponseDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.LoginRequestDTO;
import Um6p.Project.DigitalCulture.SpendSmart.dtos.UserDTO;
import Um6p.Project.DigitalCulture.SpendSmart.security.JwtUtils;
import Um6p.Project.DigitalCulture.SpendSmart.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.registerUser(userDTO);
            return ResponseEntity.ok(ApiResponseDTO.success("User registered successfully", createdUser));
        } catch (RuntimeException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateToken(userDetails);

            Map<String, String> tokenData = new HashMap<>();
            tokenData.put("token", jwtToken);
            tokenData.put("email", userDetails.getUsername());

            log.info("User logged in: {}", userDetails.getUsername());
            return ResponseEntity.ok(ApiResponseDTO.success("Login successful", tokenData));

        } catch (BadCredentialsException e) {
            log.warn("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body(ApiResponseDTO.error("Invalid email or password"));
        }
    }
}
