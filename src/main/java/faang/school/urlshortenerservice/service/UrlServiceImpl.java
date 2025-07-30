package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Override
    public List<Url> findExpiredUrl() {
        return urlRepository.getExpiredUrlsHashes();
    }

    @CachePut(cacheManager = "cacheManager",
            cacheNames = "hash",
            key = "#result"
    )
    @Transactional
    @Override
    public String createShortUrl(UrlRequestDto urlRequest) {
        String hash = hashCache.getHashFromQueue();
        Url url = Url.builder()
                .hash(hash)
                .url(urlRequest.getUrlDto())
                .expirationTime(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        return hash;
    }

    @CachePut(cacheManager = "cacheManager",
            cacheNames = "hash",
            key = "#hash"
    )
    @Override
    public String findUrlByHash(String hash) {
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> {
                    log.error("Url By Hash Not Found");
                    return new NoSuchElementException("Url not found");
                });
        return url.getUrl();
    }

    @Override
    public int countOldUrl() {
        return urlRepository.countOfOldUrl();
    }
}
