package faang.school.urlshortenerservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import faang.school.urlshortenerservice.cache.RedisService;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final HashGenerator hashGenerator;

    private final Base62Encoder base62Encoder;
    private final RedisService redisService;
    private final UrlRepository urlRepository;
    @Value("${services.hash-service.hash-cache}")
    private String hashCacheRedisKey;
    @Value("${services.hash-service.small-batch-size-unique-numbers}")
    private int smallBatchSize;

    @Override
    public UrlResponseDto shortenUrl(UrlRequestDto dto) {
        Optional<List<Hash>> optionalHashes = redisService.get(hashCacheRedisKey, new TypeReference<>() {
        });

        List<Hash> hashes = optionalHashes.orElseGet(() -> {
            log.info("Hashes not found in cache, fetching from database");
            hashGenerator.generateBatchHashes(smallBatchSize).join();
            return redisService.get(hashCacheRedisKey, new TypeReference<List<Hash>>() {
                    })
                    .orElse(Collections.emptyList());
        });

        Hash hash = hashes.get(0);
        saveUrl(dto.getUrl(), hash);
        hashes.remove(0);

        return new UrlResponseDto(hash.getHash());
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

    @Transactional
    private void saveUrl(String originalUrl, Hash hash) {
        Url url = Url.builder().id(hash.getId()).hash(hash.getHash()).originalUrl(originalUrl).build();
        urlRepository.save(url);
        redisService.save(hash.getHash(), url);
    }
}