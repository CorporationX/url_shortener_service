package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record ErrorDto(
        int status,
        String message,
        String path
) {
}
