package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
        @NotBlank
        @Size(max = 2000)
        @URL
        String longUrl,

        @Positive
        int ttlSeconds
) {
}
