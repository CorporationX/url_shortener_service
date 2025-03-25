package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ErrorDto(
        @NotBlank
        String errorMessage
) {
}
