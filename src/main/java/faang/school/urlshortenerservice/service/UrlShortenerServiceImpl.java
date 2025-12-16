package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerServiceImpl implements UrlShortenerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlShortenerConfig urlShortenerConfig;

    @Override
    @Transactional
    public void deleteOneYearOldUrl() {
        List<String> hashes = urlRepository.deleteOldUrlHashes();
        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
            log.info("Deleted {} URLs and restored {} hashes", hashes.size(), hashes.size());
        } else {
            log.info("No old URLs for delete");
        }
    }

    @Override
    @Transactional
    public String createShortUrl(CreateUrlRequestDto createUrlRequestDto) {
        String gotHash = hashCache.getHash()
                .orElseThrow(() -> new IllegalArgumentException("No free hashes available"));
        Url url = Url.builder()
                .hash(gotHash)
                .url(createUrlRequestDto.url())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(gotHash, createUrlRequestDto.url());

        return String.format("%s/%s", urlShortenerConfig.getUrlPrefix(), gotHash);
    }

    @Override
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        Url url;
        try {
            url = urlRepository.findByHash(hash);
        } catch (Exception exception) {
            throw new UrlNotFoundException(String.format("Url not found by hash %s", hash));
        }
        urlCacheRepository.save(hash, url.getUrl());
        return url.getUrl();
    }
}
