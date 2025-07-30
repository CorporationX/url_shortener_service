package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class UrlMapper {
    @Value("${app.url.prefix}")
    private String startUrl;

    public UrlResponse toUrlResponse(Url url) {
        return UrlResponse.builder()
                .url(url.getUrl())
                .shortUrl(URI.create(startUrl.concat(url.getHash())))
                .expireAt(url.getExpireAt())
                .build();
    }
}
