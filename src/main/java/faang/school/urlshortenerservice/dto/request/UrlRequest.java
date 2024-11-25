package faang.school.urlshortenerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(

        @NotBlank
        @URL
        String url
) {
}