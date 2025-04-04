package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.event.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCacheService hashCacheService;
    private final UrlMapper urlMapper;

    @Transactional
    public UrlReadDto createShortUrl(String url) {
        String hash = hashCacheService.getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .build();

        urlRepository.save(urlEntity);
        urlCacheRepository.saveUrl(hash, url);
        log.info("Сохранена ассоциация хэша {} с URL {}", hash, url);
        return urlMapper.toDto(urlEntity);
    }

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
