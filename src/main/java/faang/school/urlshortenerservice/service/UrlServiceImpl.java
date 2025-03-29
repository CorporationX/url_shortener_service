package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.RedisService;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final Base62Encoder base62Encoder;
    private final RedisService redisService;
    private final UrlRepository urlRepository;

    @Override
    public UrlResponseDto shortenUrl(UrlRequestDto dto) {
        List<Hash> res = base62Encoder.encode(List.of(1L, 100L, 1000L, 7777L, 99999L));

        //return new UrlResponseDto(res.get(0).getHash());
        return new UrlResponseDto(res.get(0).getHash());
    }

    @Override
    public String getOriginalUrl(String key) {
        return redisService.get(key, String.class)
                .orElseGet(() -> urlRepository.findByHash(key)
                        .map(urlEntity -> {
                            String originalUrlEntity = urlEntity.getOriginalUrl();
                            redisService.save(key, originalUrlEntity);
                            return originalUrlEntity;
                        })
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("The hash='%s' was not found in the cache or in the database", key))
                        )
                );
    }
}