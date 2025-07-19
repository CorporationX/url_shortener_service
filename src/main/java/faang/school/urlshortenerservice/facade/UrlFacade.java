package faang.school.urlshortenerservice.facade;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlFacade {
    private final UrlService urlService;
    @Value("${app.url.url-prefix}")
    private String hashUrlPrefix;

    public String getUrlByHash(String hash) {
        return urlService.getUrlByHash(hash).getUrl();
    }

    public String generateHash(UrlRequestDto urlRequestDto) {
        Url url = urlService.generateHash(urlRequestDto.url());
        return hashUrlPrefix + url.getHash();
    }
}
