package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NoAvailableHashInCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.hash.HashCache;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String originalUrl = urlRequestDto.getUrl();
        log.info("Creating short URL for: {}", originalUrl);

        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("No available hash in cache for URL: {}", originalUrl);
            throw new NoAvailableHashInCacheException("No available hash in cache now");
        }

        log.debug("Saving hash: {} in database", hash);
        hashRepository.save(Hash.builder().hash(hash).build());

        Url url = Url.builder().hash(hash).url(originalUrl).build();
        urlRepository.save(url);

        urlCacheRepository.save(hash, originalUrl);
        log.info("Successfully created short URL: hash={}, originalUrl={}", hash, originalUrl);

        return UrlResponseDto.builder().url(url.getUrl()).hash(hash).build();
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResponseDto getOriginalUrl(String hash) {
        log.info("Looking up original URL for hash: {}", hash);

        String url = urlCacheRepository.get(hash);
        if (url != null) {
            return UrlResponseDto.builder().url(url).hash(hash).build();
        }

        url = urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> {
                    String message = String.format("URL not found for hash: %s", hash);
                    log.error("URL not found in database for hash: {}", hash);
                    return new UrlNotFoundException(message);
                });

        urlCacheRepository.save(hash, url);

        log.info("Successfully retrieved original URL for hash: {}", hash);
        return UrlResponseDto.builder().url(url).hash(hash).build();
    }
}
