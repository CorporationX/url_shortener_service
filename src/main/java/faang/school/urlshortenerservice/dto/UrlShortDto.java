package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validator.UrlConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UrlShortDto(

        @NotNull(message = "Url can't be null")
        @NotEmpty(message = "Url can't be empty")
        @UrlConstraint(message = "Url not valid")
        String shortUrl
) {
}
