package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String getLongUrl(String hash) {
        String longUrl = urlCacheRepository.getFromCache(hash);
        if (longUrl == null) {
            longUrl = urlRepository.findUrlEntitiesByHash(hash);
            if (longUrl == null) {
                throw new UrlNotFoundException(String.format("URL not found for hash: %s", hash));
            }
        }
        return longUrl;
    }
}
