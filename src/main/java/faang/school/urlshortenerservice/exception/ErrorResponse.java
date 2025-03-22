package faang.school.urlshortenerservice.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends RuntimeException {
    String message;
    int status;
}