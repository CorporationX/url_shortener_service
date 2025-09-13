package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PathVariable;

public record HashDto(
        @PathVariable
        @NotBlank
        @Size(min = 1, max = 7, message = "Hash length must be between 1 and 7 characters")
        String hash
) {}
