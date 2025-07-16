package faang.school.urlshortenerservice.dto;

import lombok.Data;

@Data
public class UrlDto {
    private String url;
    private String shortUrl;
    private String userId;
}
