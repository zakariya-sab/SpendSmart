package Um6p.Project.DigitalCulture.SpendSmart.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper used by all REST endpoints.
 * Ensures a consistent response format across the entire API:
 * { "success": true/false, "message": "...", "data": { ... } }
 *
 * @param <T> the type of the data payload in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {

    /** Whether the operation completed successfully */
    private boolean success;

    /** Human-readable message describing the result */
    private String message;

    /** The response payload — can be any type (entity, list, JWT token, etc.) */
    private T data;

    /**
     * Creates a successful API response with data and a message.
     *
     * @param message descriptive success message
     * @param data    the response payload
     * @param <T>     the type of the data payload
     * @return a populated ApiResponseDTO with success = true
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a failed API response with an error message.
     *
     * @param message descriptive error message
     * @param <T>     the type parameter (data will be null)
     * @return a populated ApiResponseDTO with success = false
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
