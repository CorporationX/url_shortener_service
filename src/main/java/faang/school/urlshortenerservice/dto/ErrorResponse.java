package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        Instant timeStamp,
        int statusCode,
        String error,
        String message,
        String path
) {
}
