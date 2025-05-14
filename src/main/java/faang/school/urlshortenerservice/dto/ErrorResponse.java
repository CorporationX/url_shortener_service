package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private HttpStatus status;

    public int getStatus() {
        return status.value();
    }

    public String getError() {
        return status.getReasonPhrase();
    }
}