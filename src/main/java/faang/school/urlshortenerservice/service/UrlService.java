package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrl(hash);
        if(cachedUrl != null){
            log.info("Found URL in cache: {}", cachedUrl);
            return cachedUrl;
        }
        return urlRepository.findByHash(hash)
                .map(url -> {
                    String urlDb = url.getUrl();
                    log.info("Found URL in DB: {}", url);
                    urlCacheRepository.saveUrl(hash, urlDb);
                    return urlDb;
                })
                .orElseThrow(()-> new EntityNotFoundException("URL-адрес не найден для хэша: " + hash));
    }
}
