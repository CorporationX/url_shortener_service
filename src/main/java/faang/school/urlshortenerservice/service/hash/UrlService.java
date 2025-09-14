package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.NewUrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    @Value("${url.protocol}")
    private String protocol;

    @Value("${url.domain}")
    private String domain;

    private final LocalCacheService localCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    public NewUrlResponseDto createShort(@NonNull CreateUrlDto createUrlDto) {
        log.info("Creating a shor URL for: {}", createUrlDto.targetUrl());
        String hash = localCacheService.getHash();
        Url url = urlMapper.toEntity(createUrlDto, hash);
        urlRepository.save(url);
        urlCacheRepository.put(hash, createUrlDto.targetUrl());
        return new NewUrlResponseDto(composeShortUrl(hash));
    }

    private String composeShortUrl(@NonNull String hash) {
        return String.format("%s://%s/%s", protocol, domain, hash);
    }

    public String getOriginal(@NonNull HashDto hashDto) {
        String hash = hashDto.hash();
        String originalUrlCached = urlCacheRepository.get(hash);
        if (originalUrlCached != null) {
            log.info("Found cached URL in redis. Hash: {}, URL: {}", hash, originalUrlCached);
            return originalUrlCached;
        }

        Optional<Url> maybeOriginalUrlDb = urlRepository.findByHash(hash);
        if (maybeOriginalUrlDb.isPresent()) {
            log.info("Found URL in DB. Hash: {}, URL: {}", hash, maybeOriginalUrlDb.get().getUrl());
            urlCacheRepository.put(hash, maybeOriginalUrlDb.get().getUrl());
            return maybeOriginalUrlDb.get().getUrl();
        }

        throw new EntityNotFoundException("Did not find URL by requested hash: " + hash);
    }
}
