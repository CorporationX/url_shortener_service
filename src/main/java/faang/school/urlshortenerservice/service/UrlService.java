package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository cache;
    private final UrlRepository repo;

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return cache.find(hash)
                .orElseGet(() -> {
                    Url entity = repo.findById(hash)
                            .orElseThrow(() -> new UrlNotFoundException(hash));
                    String longUrl = entity.getUrl();
                    cache.save(hash, longUrl);
                    return longUrl;
                });
    }
}
