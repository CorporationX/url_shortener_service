package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@JsonAutoDetect
public record UrlResponseDto(

        @NotBlank
        String hash,

        @NotBlank
        String url,

        String shortUrl
) {
}
