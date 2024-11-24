package faang.school.urlshortenerservice.dto;

import lombok.Data;

@Data
public class UrlResponseDto {
    private String hash;
    private String url;
}
