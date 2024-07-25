package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;


    @Transactional
    public String shortenUrl(String url) {
        Url shortUrl = urlRepository.findByUrl(url)
                .orElseGet(() -> Url.builder()
                        .url(url)
                        .hash(hashCache.getHash().toString())
                        .build());

        shortUrl.setLastReceivedAt(now());

        shortUrl = urlRepository.save(shortUrl);

        return String.format("%s:%s/%s", host, port, shortUrl.getHash());
    }

    @Cacheable(value = "urlCache")
    @Transactional
    public String getUrl(String hash) {
        Url url = urlRepository.findByHash(hash);
        url.setLastReceivedAt(now());

        return url.getUrl();
    }
}