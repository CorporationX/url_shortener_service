package faang.school.urlshortenerservice.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final Instant timestamp = Instant.now();
    private final String message;
}
