package faang.school.urlshortenerservice.manager;

import faang.school.urlshortenerservice.cache.redis.UrlCache;
import faang.school.urlshortenerservice.exception.ValidationException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class UrlRepositoryManager {
    private final UrlRepository urlRepository;
    private final UrlCache urlCache;

    @Transactional
    public void save(Url url) {
        urlRepository.save(url);
        urlCache.put(url);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String urlFromCache = urlCache.get(hash);
        if (urlFromCache == null) {
            Url url = urlRepository.findById(hash)
                    .orElseThrow(() -> new ValidationException("Not found Url with hash: " + hash));
            CompletableFuture.runAsync(() -> urlCache.put(url));
            return url.getUrl();
        }
        return urlFromCache;
    }

    public List<String> getExpiredHashesAndDelete(LocalDate expirationDate) {
        List<String> expiredHashes = urlRepository.getExpiredHashesAndDelete(expirationDate);
        if (!expiredHashes.isEmpty()) {
            urlCache.remove(expiredHashes);
        }
        return expiredHashes;
    }
}
