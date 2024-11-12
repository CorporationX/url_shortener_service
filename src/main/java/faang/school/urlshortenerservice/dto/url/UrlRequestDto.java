package faang.school.urlshortenerservice.dto.url;

import org.hibernate.validator.constraints.URL;

public record UrlRequestDto(@URL(message = "Incorrect URL") String url) {
}
