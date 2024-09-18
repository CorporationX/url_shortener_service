package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCash;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    @Value("${hash.url_prefix}")
    private String urlPrefix;

    @Transactional
    public String getShortUrl(UrlDtoRequest request) {
        String hash = hashCash.getHash();
        saveShortUrl(hash, request);
        return urlPrefix + hash;
    }

    @Transactional(readOnly = true)
    public String getUrlFromHash(String hash) {
        Optional<String> cacheUrl = urlCacheRepository.getUrlByHash(hash);
        if (cacheUrl.isPresent()) {
            return cacheUrl.get();
        }
        Url entity = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url with " + hash + " not found!"));
        return entity.getUrl();
    }

    @Transactional
    public void deleteOldUrls() {
        LocalDateTime fromDate = LocalDateTime.now().minusYears(1L);
        List<String> hashes = urlRepository.removeOldUrlAndGetHashes(fromDate);
        if (!hashes.isEmpty()) {
            hashRepository.saveAll(
                    hashes.stream()
                            .map(string -> Hash.builder().hash(string).build())
                            .toList()
            );
        }
    }

    private void saveShortUrl(String hash, UrlDtoRequest request) {
        Url url = Url.builder()
                .url(request.getUrl())
                .hash(hash)
                .build();
        urlRepository.save(url);
        urlCacheRepository.saveUrlByHash(hash, request.getUrl());
    }
}