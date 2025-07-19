package faang.school.urlshortenerservice.exception.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String, String> fieldError;
    private Date timestamp;

    public ErrorResponse(int status, String message, Map<String, String> fieldError) {
        this.status = status;
        this.message = message;
        this.fieldError = fieldError;
        this.timestamp = new Date(System.currentTimeMillis());
    }
}