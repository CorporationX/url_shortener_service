package faang.school.urlshortenerservice.exception.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private Date timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date(System.currentTimeMillis());
    }
}