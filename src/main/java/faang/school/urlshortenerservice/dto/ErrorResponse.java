package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse(
        String code,
        Instant timestamp,
        int status,
        String message,
        List<String> details
) {
}

