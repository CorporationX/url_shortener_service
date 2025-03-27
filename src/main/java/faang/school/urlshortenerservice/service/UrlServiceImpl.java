package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.RedisService;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final Base62Encoder base62Encoder;
    private final RedisService redisService;
    private final UrlRepository urlRepository;

    @Override
    public UrlDto shortenUrl(UrlDto dto) {
        List<Hash> res = base62Encoder.encode(List.of(1L, 100L, 1000L, 7777L, 99999L));
        return dto;
    }

    @Override
    public String getOriginalUrl(String key) {
        String originalUrl = redisService.get(key, String.class)
                .orElseGet(() -> {
                    var urlEntity = urlRepository.findByHash(key);
                    if (urlEntity != null) {
                        String originalUrlEntity = urlEntity.getOriginalUrl();
                        redisService.save(key, originalUrlEntity);
                        return originalUrlEntity;
                    }
                    throw new EntityNotFoundException(String.format("Resource with key = %s not found", key));
                });

        return originalUrl;
    }
}