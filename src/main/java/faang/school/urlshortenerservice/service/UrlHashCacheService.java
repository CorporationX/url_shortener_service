package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.redis.RedisConfig;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlHashCacheService {

    private final UrlService urlService;
    @Value("${app.short-url}")
    private String shortUrl;

    public UrlResponseDto createShortUrl(UrlRequestDto urlRequest) {
        String hashUrl = urlService.createShortUrl(urlRequest);

        URI newUrl = UriComponentsBuilder
                .fromHttpUrl(shortUrl)
                .path(hashUrl)
                .build()
                .toUri();
        return UrlResponseDto.builder()
                .urlResponseDto(newUrl)
                .build();
    }

    public UrlResponseDto getShortUrl(String hash) {
        String url = urlService.findUrlByHash(hash);

        URI newUrl = UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .toUri();

        return UrlResponseDto.builder()
                .urlResponseDto(newUrl)
                .build();
    }
}
