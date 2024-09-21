package faang.school.urlshortenerservice.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String url;
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}