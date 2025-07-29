package faang.school.urlshortenerservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(description = "Errors and exceptions presentation")
public class ErrorResponseDto {
    @Schema(description = "Error status code")
    private final String status;
    @Schema(description = "Error reason")
    private final String reason;
    @Schema(description = "Error message")
    private final String message;
    @Schema(description = "Error appear timestamp")
    private final String timestamp;
}
