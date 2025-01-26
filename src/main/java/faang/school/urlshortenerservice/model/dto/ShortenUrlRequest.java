package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Builder
public record ShortenUrlRequest(

        @NotBlank
        @URL
        String url,

        @Future
        LocalDateTime expiredAt
) {
}
