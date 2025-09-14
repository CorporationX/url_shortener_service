package faang.school.urlshortenerservice.entity;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String details;

    public ErrorResponse(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
}