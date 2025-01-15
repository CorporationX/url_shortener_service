package faang.school.urlshortenerservice.handler;

import java.time.LocalDateTime;

public record ErrorResponse(String error, String message, LocalDateTime timestamp) {
}
