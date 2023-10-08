package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${application.host:localhost}")
    private String host;

    private final UrlRepository repository;
    private final HashCache cache;
    public String createShortenedUrl(UrlDto url) {
        String hash = cache.getHash();
        String shortenedUrl = "http://" +
                host +
                "/" +
                hash;
        saveUrl(hash, url);

        return shortenedUrl;
    }

    private void saveUrl(String shortenedUrl, UrlDto url) {
        Url urlEntity = Url.builder()
                .hash(shortenedUrl)
                .url(url.getUrl())
                .build();

        repository.save(urlEntity);
    }
}
