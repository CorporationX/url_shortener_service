package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

public record UrlDto(

        @URL
        @Size(min = 8, max = 1024, message = "The URL length must be between 8 and 1024 characters")
        String url
) implements Serializable {
}
