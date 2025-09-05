package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequestDto (@NotBlank(message = "URL is empty")
                             @URL(message = "URL must start with http:// or https://")
                             String url) {
}