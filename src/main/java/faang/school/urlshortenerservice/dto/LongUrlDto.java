package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LongUrlDto(@NotBlank (message = "URL cannot be empty") String url) {}
