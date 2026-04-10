/**
 * Generic API response wrapper matching the Spring Boot ApiResponseDTO.
 * All API endpoints return data in this format:
 * { success: boolean, message: string, data: T }
 *
 * @template T the type of the data payload
 */
export interface ApiResponse<T> {
  /** Whether the operation was successful */
  success: boolean;
  /** Human-readable result message */
  message: string;
  /** The response data payload */
  data: T;
}
