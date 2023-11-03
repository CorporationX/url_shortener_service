package faang.school.urlshortenerservice.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime time = LocalDateTime.now();
}
