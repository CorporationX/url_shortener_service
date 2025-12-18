package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record CreateUrlDto(

        @NotBlank(message = "URL cannot be empty")
        @URL(message = "Must be valid url")
        String url
) {
}