package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.utils.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.protocol}")
    private String protocol;
    @Value("${url.domain}")
    private String domain;
    @Value("${server.port}")
    private int port;

    @Transactional
    public UrlResponse getShortUrl(UrlRequest url) {
        String longUrl = url.getLongUrl();
        String hash = urlRepository.getHashByLongUrl(longUrl);

        if (hash == null) {
            hash = hashCache.getHash();
            urlRepository.save(hash, longUrl);
        }
        urlCacheRepository.save(hash, longUrl);

        String shortUrl = protocol + "://" + domain + ":" + port + "/" + hash;
        return new UrlResponse(shortUrl);
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        String url = urlCacheRepository.getUrlByHash(hash);
        if (url == null) {
            url = urlRepository.getUrlByHash(hash);
            if (url == null) {
                log.error("Url not found for hash: {}", hash);
                throw new UrlNotFoundException("Url not found for hash: " + hash);
            }
            urlCacheRepository.save(hash, url);
        }

        return url;
    }
}
