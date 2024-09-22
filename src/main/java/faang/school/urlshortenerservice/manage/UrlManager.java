package faang.school.urlshortenerservice.manage;

import faang.school.urlshortenerservice.cache.UrlCacheManager;
import faang.school.urlshortenerservice.exception.NotFoundEntityException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UrlManager {
    private final UrlCacheManager urlCacheManager;
    private final UrlRepository urlRepository;


    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String urlFromCache = (String) urlCacheManager.get(hash);
        if (urlFromCache == null) {
            Url url = urlRepository.findById(hash)
                    .orElseThrow(() -> new NotFoundEntityException("Not found Url with hash: " + hash));
            CompletableFuture.runAsync(() -> urlCacheManager.add(url));
            return url.getUrl();
        }
        return urlFromCache;
    }

    public void addCache(Url url) {
        urlCacheManager.add(url);
    }

    @Transactional
    public Url saveUrl(String hash, String url) {
        return urlRepository.save(new Url(hash, url));
    }

    @Transactional
    public List<String> getExpiredHashesAndDelete(LocalDateTime dateExpired) {
        List<String> expiredHashes = urlRepository.removeExpiredHash(dateExpired);
        if (!expiredHashes.isEmpty()) {
            urlCacheManager.remove(expiredHashes);
        }
        return expiredHashes;
    }
}
