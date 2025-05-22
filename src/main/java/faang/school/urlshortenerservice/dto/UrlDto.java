package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlDto (@URL(message = "Invalid URL")
                      @NotBlank(message = "URL cannot be empty")
                      String url) {}
