package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record UrlRequestDto(

        @NotBlank
        @URL(message = "Incorrect URL format")
        String url
) {
}
