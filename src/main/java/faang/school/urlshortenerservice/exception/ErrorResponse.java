package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String error) {
}
