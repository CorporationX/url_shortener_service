package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UrlDto(@NotBlank(message = "URL cannot be null or empty")
                     @Pattern(regexp = "^(https?://)" +
                             "(([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)" +
                             "{3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(\\:[0-9]{1,5})?(/.*)?$", message = "Invalid URL format") String url) {
}
