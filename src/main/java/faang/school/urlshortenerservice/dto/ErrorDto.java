package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record ErrorDto(
        String error,
        String message
)
{  }
