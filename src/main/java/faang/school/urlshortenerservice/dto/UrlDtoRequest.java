package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;

public record UrlDtoRequest(
        @URL(regexp = "\"^(https?:\\\\/\\\\/)?([\\\\da-z\\\\.-]+)\\\\.([a-z\\\\.]{2,6})([\\\\/\\\\w \\\\.-]*)*\\\\/?$\"")
        String url) {
}
