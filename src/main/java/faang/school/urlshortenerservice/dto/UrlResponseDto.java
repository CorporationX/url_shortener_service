package faang.school.urlshortenerservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class UrlResponseDto {
    private String url;
    private String hash;
    private LocalDateTime createdAt;
}