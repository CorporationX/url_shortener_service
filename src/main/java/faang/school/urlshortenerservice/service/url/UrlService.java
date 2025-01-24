package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.util.HashCache;
import faang.school.urlshortenerservice.util.UrlBuilder;
import java.util.Optional;
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
    private final UrlBuilder uriBuilder;

    @Value("${spring.date.ttl.hour.url:24}")
    private long urlTtlInCache;

    @Transactional
    public String createHashUrl(String originalUrl) {
        String hash = hashCache.getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
        urlRepository.save(urlEntity);
        urlCacheRepository.saveByTtlInHour(urlEntity, urlTtlInCache);
        return uriBuilder.response(hash);
    }


    @Transactional(readOnly = true)
    public String getPrimalUri(String hash) {
        Optional<Url> cachedUrl = urlCacheRepository.findByHash(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get().getUrl();
        }
        Url found = urlRepository.findById(hash)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found for hash: %s", hash));
        urlCacheRepository.saveByTtlInHour(found, urlTtlInCache);
        return found.getUrl();
    }
}
