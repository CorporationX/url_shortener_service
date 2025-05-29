package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public UrlResponseDto generateShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.url())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(url.getUrl(), hash);

        log.info("Hash {} for URL {} has been created", hash, url);
        return UrlResponseDto.builder()
                .url(urlDto.url())
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public String getUrl(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("There is no link to such hash"))
                .getUrl();
    }
}
