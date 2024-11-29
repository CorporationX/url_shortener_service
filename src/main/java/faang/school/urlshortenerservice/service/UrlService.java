package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.Link;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortUrl(Link link) {
        String hash = hashCache.getHash();
        urlRepository.save(new Url(hash, link.getLink(), LocalDateTime.now()));
        urlCacheRepository.save(hash, link.getLink());
        log.info("Converted url added: {}", link.getLink());
        return hash;
    }

    @Transactional(readOnly = true)
    public String getShortUrl(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        log.info("Received - hash: {}", hash);
        if (cachedUrl == null) {
            Url url = urlRepository.findByHash(hash);
            if (url != null) {
                cachedUrl = url.getUrl();
            } else {
                log.error("Url not found!");
                throw new EntityNotFoundException("URL NOT FOUND!");
            }
        }
        log.info("Converted url received: {}", cachedUrl);
        return cachedUrl;
    }
}
