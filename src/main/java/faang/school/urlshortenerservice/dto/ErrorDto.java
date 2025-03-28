package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Builder
public record ErrorDto(

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant timestamp,
        String path,
        String method,
        String message
) {
}
