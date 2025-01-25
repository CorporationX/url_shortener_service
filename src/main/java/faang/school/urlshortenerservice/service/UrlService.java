package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.hesh.HashCache;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCoach;
    private final HashJpaRepository hashRepository;
    private final UrlJpaRepository urlRepository;
    private final RedisCacheRepository redisCacheRepository;
    @Value("${url.original-path}")
    private String urlPath;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String hash = hashCoach.getHash();
        log.info("Generated hash: {}", hash);
        Url urlEntity = new Url(hash, urlDto.url(), LocalDateTime.now());

        urlRepository.save(urlEntity);
        redisCacheRepository.save(hash, urlDto.url());
        return urlPath.concat(hash);
    }

    @Transactional
    public String getOriginalUrl(String hash) {
        String cachedUrl = redisCacheRepository.get(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        try {
            Url urlEntity = urlRepository.findByHash(hash);
            if (urlEntity == null) {
                throw new DataNotFoundException("Url with hash %s was not found in database".formatted(hash));
            }

            cachedUrl = urlEntity.getUrl();

            redisCacheRepository.save(hash, cachedUrl);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Url with hash %s was not found in database".formatted(hash));
        }

        return cachedUrl;
    }

    @Transactional
    public void cleaner() {
        List<String> expiredHashes = urlRepository.deleteExpiredUrlsReturningHashes();
        if (!expiredHashes.isEmpty()) {
            hashRepository.batchSave(expiredHashes);
        }
        redisCacheRepository.deleteAll(expiredHashes);
    }

}

