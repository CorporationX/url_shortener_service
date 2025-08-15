package faang.school.urlshortenerservice.kafka.dto;

import lombok.Builder;

@Builder
public record ShortUrlVisitDto(
        String code
) {
}
