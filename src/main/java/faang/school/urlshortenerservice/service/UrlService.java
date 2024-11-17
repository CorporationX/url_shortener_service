package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalHashCache;
import faang.school.urlshortenerservice.service.cache.UrlRedisCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlGenerator urlGenerator;
    private final UrlRedisCacheService urlRedisCacheService;
    private final LocalHashCache localHashCache;

    @Transactional(readOnly = true)
    public Url getOriginalUrl(String hash) {
        String url = urlRedisCacheService.get(hash)
                .orElseGet(
                        () -> getUrl(hash).getUrl()
                );
        return Url.builder().hash(hash).url(url).build();
    }

    @Transactional
    public List<String> getAndDeleteUnusedHashes() {
        return urlRepository.findAndDeleteUnusedHashes();
    }

    @Transactional
    public String makeShortUrl(String receivedUrl) {
        String hash = localHashCache.getHash();
        String shortUrl = urlGenerator.makeShortUrl(hash);

        Url urlEntity = Url.builder()
                .hash(hash)
                .url(receivedUrl)
                .build();

        urlRepository.save(urlEntity);
        urlRedisCacheService.save(hash, receivedUrl);
        return shortUrl;
    }

    @Transactional
    public Url getUrl(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("There is no Url for hash " + hash));
    }
}
