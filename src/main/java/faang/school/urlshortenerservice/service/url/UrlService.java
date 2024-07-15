package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.search.SearchesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final List<SearchesService> urlServices;

    @Transactional
    public String transformUrlToHash(String url) {
        log.info("Transforming URL to hash: {}", url);
        String hashValue = hashCache.getRandomHashFromCache();
        log.info("Retrieved hash from cache: {}", hashValue);

        urlRepository.save(Url.builder().url(url).hash(hashValue).createdAt(LocalDateTime.now()).build());
        log.info("Saved URL to repository: {} with hash: {}", url, hashValue);

        hashCache.saveToCache(hashValue, url);
        log.info("Saved hash to cache: {} with URL: {}", hashValue, url);

        return hashValue;
    }

    @Transactional
    public String getUrlFromHash(String hash) {
        log.info("Retrieving URL for hash: {}", hash);
        return urlServices.stream().map(service -> service.findUrl(hash)).filter(Optional::isPresent)
                .map(Optional::get).findFirst().orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash));
    }
}
