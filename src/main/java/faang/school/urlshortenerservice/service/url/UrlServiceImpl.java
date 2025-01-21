package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.exception.UrlException;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.validator.url.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlValidator urlValidator;
    private final UrlCacheRepository urlCacheRepository;
    private final HashService hashService;

    @Override
    @Transactional
    public String shortenUrl(UrlDto urlDto) {
        var urlOptional = urlValidator.findByUrl(urlDto.getUrl());
        if (urlOptional != null) {
            return urlOptional.getHash();
        }
        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("Failed to generate short URL");
            throw new RuntimeException("Failed to generate short URL");
        }
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.getUrl());
        log.info("created shortened URL: {}", hash);
        return hash;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "url", key = "#hash")
    public String getOriginalUrl(String hash) {
        log.info("Retrieving original URL for hash: {}", hash);
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlException("URL not found"));
    }

    @Override
    @Transactional
    public void cleanExpiredUrls() {
        Set<String> hashes = urlRepository.findExpiredUrls()
                .orElseThrow(() -> new UrlException("No expired URLs found"));
        hashService.addHashes(hashes);
    }
}
