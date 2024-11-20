package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.annotation.Url;
import lombok.Data;

@Data
public class UrlDto {

    @Url
    private String url;
}
