package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.сache.HashCache;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final HashService hashService;

    @Async("executorService")
    @Transactional
    public CompletableFuture<String> save(UrlDto urlDto) {
        Hash hash = hashCache.getHash();
        Url url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash.getHash())
                .build();

        if (!urlRepository.existsByUrl(url.getUrl())) {
            urlRepository.save(url);
            urlCacheRepository.saveUrl(url);
        } else {
            log.error(String.format("This url %s already exists", url.getUrl()));
            throw new EntityExistsException("Url already exists");
        }

        return CompletableFuture.completedFuture(hash.getHash());
    }

    @Transactional
    public String getUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash);
        if (url == null) {
            url = urlRepository.findById(hash).orElseThrow(() -> {
                String errorMessage = String.format("Url with hash: %s not found", hash);
                log.error(errorMessage);
                return new EntityNotFoundException(errorMessage);
            }).getUrl();

            urlCacheRepository.saveUrl(Url.builder()
                    .hash(hash)
                    .url(url)
                    .build());
        }
        return url;
    }

    @Transactional
    public void deleteUrlsOlderThanOneYear() {
        List<Hash> hashes = urlRepository.deleteOlderOneYearUrls();

        hashService.saveBatch(hashes);
    }
}
