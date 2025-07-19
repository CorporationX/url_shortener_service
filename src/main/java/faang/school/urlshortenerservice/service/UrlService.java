package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Cacheable(value = "urlCache", key = "#hash")
    @Transactional(readOnly = true)
    public Url getUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Hash %s not found", hash);
                    log.error(errorMsg);
                    return new HashNotFoundException(errorMsg);
                });
    }

    @CachePut(value = "urlCache", key = "#result.hash")
    @Transactional
    public Url generateHash(String url) {
        String hash = hashCache.getHash();
        Url urlEntity = new Url();
        urlEntity.setUrl(url);
        urlEntity.setHash(hash);

        Url savedUrl = urlRepository.save(urlEntity);
        log.info("Url {} has been saved", savedUrl);

        return savedUrl;
    }
}
