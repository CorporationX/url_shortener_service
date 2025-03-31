package faang.school.urlshortenerservice.exception;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String errorCode;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errorCode = "ERR_" + status;
    }
}