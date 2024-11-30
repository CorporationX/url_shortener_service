package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public void cleanOldUrls() {
        List<Hash> hashList = urlRepository.getHashAndDeleteURL().stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashList);
        log.info("Removed {} hashes.", hashList.size());
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrlByHash(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        Url url = urlRepository.findUrlByHash(hash).orElseThrow(HashNotFoundException::new);

        urlCacheRepository.save(url);
        return url.getUrl();
    }

    @Transactional
    public String createShortLink(Url url) {
        String newHash = hashCache.getHash();

        Url newUrl = Url.builder()
                .url(url.getUrl())
                .hash(newHash)
                .build();

        Url savedUrl = urlRepository.save(newUrl);
        urlCacheRepository.save(savedUrl);

        return savedUrl.getHash();

    }
}
