package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    @Value("${server.domain}")
    private String domain;

    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String hash = getHash();
        UrlDto urlDto = buildUrlDto(hash, urlRequestDto.getUrl());

        saveUrlDto(urlDto);
        cacheUrlDto(urlDto);

        return UrlResponseDto.builder()
                .url(domain + "/" + hash)
                .build();
    }

    public UrlDto getUrl(String hash) {
        String url = getCachedOrPersistedUrl(hash);

        return UrlDto.builder()
                .hash(hash)
                .url(url)
                .build();
    }

    private String getCachedOrPersistedUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrl(hash);

        if (cachedUrl != null) {
            return cachedUrl;
        } else {
            return urlRepository.findById(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash));
        }
    }

    private String getHash() {
        return hashCache.getHash();
    }

    private void saveUrlDto(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        urlRepository.save(url);
    }

    private void cacheUrlDto(UrlDto urlDto) {
        urlCacheRepository.save(urlDto.getHash(), urlDto.getUrl());
    }

    private UrlDto buildUrlDto(String hash, String url) {
        return UrlDto.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}