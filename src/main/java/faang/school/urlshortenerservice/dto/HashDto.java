package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;

public record HashDto(@NotNull(message = "Url не может отсутствовать.") String hash) {
}
