package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UrlShortener {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;
    private final HashCache hashCache;
    private final UrlService urlService;

    @Transactional
    public String shortenUrl(String url) {
        Url shortUrl = getShortUrl(url);
        urlService.saveUrl(shortUrl);

        return host + ":" + port + "/" + shortUrl.getHash();
    }

    private Url getShortUrl(String url) {
        return urlService.findByUrl(url).orElseGet(
                () -> Url.builder()
                        .url(url)
                        .hash(hashCache.getHash().toString())
                        .build()
        );
    }

    @Cacheable(value = "urlCache")
    @Transactional
    public String getUrl(String hash) {
        Url url = urlService.findByHash(hash);
        return url.getUrl();
    }
}
