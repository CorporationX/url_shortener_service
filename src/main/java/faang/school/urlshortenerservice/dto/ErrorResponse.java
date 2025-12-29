package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing error details")
public class ErrorResponse {
    
    @Schema(description = "Error message", example = "URL not found for hash: abc123")
    private String message;
    
    @Schema(description = "Timestamp when error occurred")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path where error occurred", example = "/abc123")
    private String path;

    public ErrorResponse(String message, String path) {
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}

