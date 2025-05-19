package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String message
) {}
