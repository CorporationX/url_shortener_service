package faang.school.urlshortenerservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlReadDto {
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime createdAt;
}
