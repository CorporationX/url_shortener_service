package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlDto(
        @URL(message = "incorrect input please use a correct url")
        String url
) {}
