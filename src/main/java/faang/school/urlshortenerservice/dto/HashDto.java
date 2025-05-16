package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record HashDto(@NotBlank(message = "Hash cannot be empty") String hash) {
}
