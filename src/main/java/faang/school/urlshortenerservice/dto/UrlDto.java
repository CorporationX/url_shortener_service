package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlDto(
    @URL(message = "Incorrect link format")
    String url
) {
}
