package faang.school.urlshortenerservice.exception;

import lombok.Value;

@Value
public class ErrorResponse extends RuntimeException {
    String message;
    int status;
}