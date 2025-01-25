package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlReq(@URL(message = "Invalid URL format") @NotBlank String url) {
}
