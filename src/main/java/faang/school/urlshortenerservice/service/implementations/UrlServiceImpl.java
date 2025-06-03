package faang.school.urlshortenerservice.service.implementations;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.interfaces.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    @Override
    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        String hash = hashCache.getHash();
        String originalUrl = urlRequest.getUrl();
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, originalUrl);
        log.info("Created short url with hash: {}\noriginal url: {}", hash, originalUrl);

        return urlMapper.toUrlResponse(url);
    }

    @Override
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.findByHash(hash);
        if (cachedUrl != null && !cachedUrl.isBlank()) {
            log.info("Found cached URL for hash: {}", hash);
            return cachedUrl;
        }

        log.info("Did not find cached URL for hash: {}, checking database", hash);
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }
}
