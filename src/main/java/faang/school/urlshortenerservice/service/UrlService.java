package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final LocalCache localCache;

    @Transactional
    public String createShortUrl(UrlRequestDto urlDto) {
        String hash = localCache.getHash();

        Url url = Url.builder()
                .url(urlDto.url())
                .hash(hash)
                .createdAt(Instant.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.url());

        return hash;
    }

    public String getOriginalUrl(String hash) {
        return urlCacheRepository.getUrl(hash)
                .or(() -> urlRepository.findByHash(hash).map(Url::getUrl))
                .orElseThrow(() -> new UrlNotFoundException("no URL was found for this hash: " + hash));
    }
}
