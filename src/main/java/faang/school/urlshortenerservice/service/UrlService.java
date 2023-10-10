package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFound;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${application.host:localhost}")
    private String host;
    @Value("${application.protocol:http}")
    private String protocol;
    @Value("${application.port:8080}")
    private int port;
    @Value("${application.path:/url/}")
    private String path;

    private final UrlRepository repository;
    private final UrlCacheRepository cacheRepository;
    private final HashCache cache;

    public String getOriginalUrl(String shortenedUrl) {
        Optional<String> optionalUrl = cacheRepository.getByHash(shortenedUrl);

        if (optionalUrl.isPresent()) {
            return optionalUrl.get();
        }
        Optional<Url> optional = repository.getByHash(shortenedUrl);

        return optional
                .map(Url::getUrl)
                .orElseThrow(UrlNotFound::new);
    }

    public String createShortenedUrl(UrlDto url) {
        return createAndSaveShortUrl(url.getUrl());
    }

    private String createAndSaveShortUrl(String url) {
        String hash = cache.getHash();
        saveUrl(hash, url);

        return createNewUrl(hash).toString();
    }

    private URL createNewUrl(String hash) {
        String file = path + hash;
        try {
            return new URL(protocol,
                    host,
                    port,
                    file);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUrl(String shortenedUrl, String url) {
        Url urlEntity = Url.builder()
                .hash(shortenedUrl)
                .url(url)
                .build();

        repository.save(urlEntity);
    }
}
