package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlServiceProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final LocalCache localCache;
    private final UrlServiceProperties properties;

    @Override
    @Transactional
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        String hash = localCache.getHash();
        urlRepository.save(new Url(hash, urlDto.url(), LocalDateTime.now()));
        //ToDo: save to redis cache
        return new ShortUrlDto(properties.getBaseShortUrl() + hash);
    }

    @Override
    @Transactional
    public UrlDto getUrl(String hash) {
        //ToDo: find in redis cache
        Url url = urlRepository.findById(hash)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Url for hash %s not found", hash))
                );
        return new UrlDto(url.getUrl());
    }
}

