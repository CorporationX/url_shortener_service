package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record CreateShortUrlDto(
        @URL(message = "URL is invalid, please check spelling and try again")
        String originalUrl
) {
}
