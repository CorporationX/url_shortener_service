package faang.school.urlshortenerservice.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        List<String> details
) {
    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now(), null);
    }
    public ErrorResponse(String code, String message, List<String> details) {
        this(code, message, Instant.now(), details);
    }
}