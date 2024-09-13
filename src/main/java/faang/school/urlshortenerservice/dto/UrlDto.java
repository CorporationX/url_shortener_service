package faang.school.urlshortenerservice.dto;

import lombok.Data;

@Data
public class UrlDto {
    private long id;
    private String shortUrl;
    private String longUrl;
}
