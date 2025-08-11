package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.annotation.ValidUrl;
import lombok.Getter;

@Getter
public class UrlDto {
    @ValidUrl
    String url;
}
