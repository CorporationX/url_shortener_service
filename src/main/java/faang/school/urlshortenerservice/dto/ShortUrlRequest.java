package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record ShortUrlRequest(
        @NotNull(message = "Required parameter url should be not null")
        @URL(message = "Invalid url address")
        String url,
        @Future(message = "Must be in future")
        LocalDateTime expirationTime
) {
}
