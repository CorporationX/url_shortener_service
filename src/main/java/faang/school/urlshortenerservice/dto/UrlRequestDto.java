package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlRequestDto(
        @URL(message = "Incorrect URL")
        String url,

        @URL(message = "Incorrect URL")
        String shortUrl,

        String hash

        //@Future(message = "Expiration date must be in the future")
        //LocalDateTime expiredAtDate
) {
}
