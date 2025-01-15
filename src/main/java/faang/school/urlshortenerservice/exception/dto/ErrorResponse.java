package faang.school.urlshortenerservice.exception.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {
    private String message;
    private String details;
    private Map<String, String> errorsList;

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
