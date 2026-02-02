package faang.school.urlshortenerservice.exception.handler;

import jakarta.validation.constraints.NotBlank;

public record TestDto(
        @NotBlank(message = "Name cannot be null, empty or a space")
        String name
) {}
