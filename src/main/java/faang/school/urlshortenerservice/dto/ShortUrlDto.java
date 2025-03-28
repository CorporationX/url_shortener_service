package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record ShortUrlDto(

        @URL
        @Size(min = 8, max = 2048, message = "The URL length must be between 8 and 2048 characters")
        String url
) {
}
