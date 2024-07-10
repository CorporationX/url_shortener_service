package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Cacheable(cacheNames = "urls", key = "#url")
    public String createShortUrl(UrlDto url) {

        Url shortUrl = Url.builder()
                .hash(hashCache.getHash().getHash())
                .url(url.getUrl())
                .build();

        Url entity = urlRepository.save(shortUrl);
        urlCacheRepository.save(entity);

        return String.format("%s:%s/%s", host, port, shortUrl.getHash());
    }
}
