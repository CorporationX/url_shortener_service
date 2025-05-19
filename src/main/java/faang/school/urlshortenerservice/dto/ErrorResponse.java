package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(

    String timestamp,
    int status,
    String error,
    String message
) {}
