package faang.school.urlshortenerservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String url;
    private int status;
    protected String message;

    public ErrorResponse(String url, HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now();
        this.url = url;
        this.status = status.value();
        this.message = message;
    }
}