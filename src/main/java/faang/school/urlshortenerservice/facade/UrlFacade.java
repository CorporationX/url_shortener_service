package faang.school.urlshortenerservice.facade;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlFacade {
    private final UrlService urlService;

    public String getUrlByHash(String hash) {
        return urlService.getUrlByHash(hash).getUrl();
    }
}
