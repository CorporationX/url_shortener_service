package faang.school.urlshortenerservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.net.URL;

@Getter
@Builder
public class UrlDto {
    String url;
}
