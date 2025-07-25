package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlDto(
        @URL(message = "should be an URL address")
        String url
) {}