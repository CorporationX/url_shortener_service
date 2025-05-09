package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    private final UrlMapper urlMapper;

    @Transactional
    @Override
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String originalUrl = urlRequestDto.getUrl();
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new RuntimeException("No available hashes in cache");
        }
        hashRepository.save(Hash.builder().hash(hash).build());
        Url url = Url.builder().hash(hash).url(originalUrl).build();
        urlRepository.save(url);
        log.info("saved to db");
        urlCacheRepository.save(hash, originalUrl);
        log.info("saved to redis");
        return UrlResponseDto.builder().hash(hash).url(originalUrl).build();
    }

    @Override
    public UrlResponseDto getOriginalUrl(String hash) {
        String url = urlCacheRepository.get(hash);
        if (url == null) {
            url = urlRepository.findById(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> {
                        String message = String.format("URL not found for hash: %s", hash);
                        return new UrlNotFoundException(message);
                    });
            urlCacheRepository.save(hash, url);
        }
        return UrlResponseDto.builder().url(url).hash(hash).build();
    }
}
