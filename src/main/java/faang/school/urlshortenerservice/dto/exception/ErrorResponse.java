package faang.school.urlshortenerservice.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standard error response returned by the API")
public record ErrorResponse(

        @Schema(description = "Short description of the error type")
        String error,

        @Schema(description = "Detailed message describing the error")
        String message,

        @Schema(description = "Optional list of validation error details; null for non-validation errors")
        List<ValidationErrorDetail> details
) {
    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, null);
    }

    public static ErrorResponse withDetails(String error, String message, List<ValidationErrorDetail> details) {
        return new ErrorResponse(error, message, details);
    }
}