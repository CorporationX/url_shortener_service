package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UrlsDto(String hash , String url, LocalDateTime createdAt) {
}