package faang.school.urlshortenerservice.dto;

import java.util.Map;

public record ErrorResponse(int status, String message, Map<String, String> details) {
    public ErrorResponse {
        details = details != null ? Map.copyOf(details) : null;
    }
    public ErrorResponse(int status, String message) {
        this(status, message, null);
    }
}