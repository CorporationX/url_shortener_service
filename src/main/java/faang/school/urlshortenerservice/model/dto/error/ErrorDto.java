package faang.school.urlshortenerservice.model.dto.error;

import lombok.Builder;

@Builder
public record ErrorDto(
        int code,
        String message
) {
}
