package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCacheImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCacheImpl hashCache;

    @Override
    public String getLongUrl(String hash) {
        return urlCacheRepository.getAndRefresh(hash)
                .or(() -> urlRepository.findByHash(hash))
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash));
    }

    @Override
    @Transactional
    public String getShortUrl(String url) {
        String hash = hashCache.getHash();

        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlRepository.save(urlEntity);

        urlCacheRepository.save(hash, url);

        return hash;
    }

    @Override
    @Transactional
    public void cleaningOldHashes(LocalDate date) {
        List<Hash> releasedHashes = urlRepository.deleteOldUrlsAndReturnHashes(date);
        if (releasedHashes.isEmpty()) {
            hashRepository.saveBatch(releasedHashes);
        }
    }
}
