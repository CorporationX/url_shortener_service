package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UnValidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.URLToStringMapper;
import faang.school.urlshortenerservice.repository.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashService hashService;
    private final URLToStringMapper urlToStringMapper;
    private final HashCache hashCache;

    @Value("${url.short-header:http:\\\\localhost:8080}")
    private String shortUrlHeader;
    @Value("${url.ttl-in-mounts:3}")
    private Long defaultTtl;

    @Transactional
    public String getUrl(String hash) throws UrlNotFoundException {
        String url = urlCacheRepository.getUrl(hash);
        if (url == null) {
            url = urlRepository.getUrlByHash(hash);
            urlCacheRepository.setUrl(hash, url);
        }

        if (url == null) {
            throw new UrlNotFoundException("Url with hash %s not found".formatted(hash));
        }
        return url;
    }

    @Transactional
    public String createShortUrl(String urlOriginal, LocalDateTime deleteAt) throws UnValidUrlException {
        URL urlFromOriginal = urlToStringMapper.convertToEntityAttribute(urlOriginal);

        if (deleteAt == null) {
            deleteAt = LocalDateTime.now().plusMonths(defaultTtl);
        }

        String hash = urlRepository.findHashByUrl(urlOriginal);
        if (hash != null) {
            return "%s/%s".formatted(shortUrlHeader, hash);
        }

        hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlFromOriginal)
                .hash(hash)
                .deletedAt(deleteAt)
                .build();
        urlRepository.save(url);
        urlCacheRepository.setUrl(hash, urlOriginal);

        return "%s/%s".formatted(shortUrlHeader, hash);
    }

    @Transactional
    public Long deleteOldUrl() {
        List<String> deletedUrls = urlRepository.deleteOldUrls();
        deletedUrls.forEach(urlCacheRepository::removeUrl);
        return hashService.addHashList(deletedUrls);
    }
}
