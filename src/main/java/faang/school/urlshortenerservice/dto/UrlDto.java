package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UrlDto(
        @NotEmpty(message = "Original url must be not empty and not null")
        String url,
        @Future(message = "Date must be future")
        LocalDateTime deleteAt
) {
}
