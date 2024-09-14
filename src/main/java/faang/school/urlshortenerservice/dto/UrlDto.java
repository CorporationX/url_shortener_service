package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;

public record UrlDto(@NotNull(message = "Url не может отсутствовать.") String url) {
}
