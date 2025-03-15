package faang.school.urlshortenerservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlReadDto {
    private String originalUrl;
    private String hash;
    private LocalDateTime createdAt;
}
