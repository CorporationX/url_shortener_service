package faang.school.urlshortenerservice.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    HttpStatus status,
    String errorCode,
    String errorMessage
) {

}
