package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlDto(
        @URL(message = "must be an URL")
        String url
) {}
