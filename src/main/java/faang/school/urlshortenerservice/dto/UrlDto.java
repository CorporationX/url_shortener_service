package faang.school.urlshortenerservice.dto;

import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

@Validated
public record UrlDto(@URL(message = "Неправильный формат Url") String url) {
}
