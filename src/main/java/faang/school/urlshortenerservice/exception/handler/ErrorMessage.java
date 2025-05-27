package faang.school.urlshortenerservice.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class ErrorMessage {

    private String message;
    private LocalDateTime timestamp;

    public ErrorMessage(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
