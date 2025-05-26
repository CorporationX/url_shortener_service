package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ExceptionResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
