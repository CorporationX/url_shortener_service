package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashLocalCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dao.HashDao;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UrlServiceImpl implements UrlService {
    private final HashLocalCache hashLocalCache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    private final HashDao hashDao;

    @Transactional
    public String processLongUrl(@NotNull(message = "UrlDto cannot be NULL") UrlDto urlDto) {
        try {
            String hash = hashLocalCache.getHash();
            Url url = Url.builder()
                    .hash(hash)
                    .url(urlDto.getUrl())
                    .build();
            Url savedUrl = urlRepository.save(url);

            urlCache.addToCache(savedUrl.getHash(), savedUrl.getUrl());
            return savedUrl.getHash();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> deleteOldReturningHashes(@NotNull LocalDateTime cutoff, @Min(1) int batchLimit) {
        List<String> expiredHashes = urlRepository.deleteOldReturningHashes(cutoff, batchLimit);
        hashDao.insertHashes(expiredHashes);
        return expiredHashes;
    }

    @Override
    @Cacheable(value = "urls", key = "#hash", unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    public String getOriginalUrl(@NotBlank String hash) {
        log.info("getOriginalUrl from DB: {}", hash);
        Url url = urlRepository.findById(hash).orElseThrow(() -> new HashNotFoundException(hash));
        return url.getUrl();
    }
}