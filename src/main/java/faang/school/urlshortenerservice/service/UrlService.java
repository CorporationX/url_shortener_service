package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;


    public HashDto createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        HashDto hashDto = new HashDto(hash);
        String url = urlDto.url();

        urlRepository.save(hash, url);
        urlCacheRepository.saveRedisCache(hash, url);

        log.info("Hash {} for URL {} has been created", hash, url);
        return hashDto;
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getRedisCache(hash);
        if (originalUrl != null) {
            log.info("Hash {} for URL {} has been retrieved from cache", hash, originalUrl);
            return originalUrl;
        }

        originalUrl = urlRepository.get(hash);
        if (originalUrl == null) {
            log.warn("URL not found for hash {}", hash);
            throw new ResponseStatusException(NOT_FOUND, "URL not found for hash " + hash);
        }

        urlCacheRepository.saveRedisCache(hash, originalUrl);
        log.info("Url for hash {} loaded from cache", hash);
        return originalUrl;
    }

    public List<String> clearOldUrls() {
        log.info("Clearing old urls");
        return urlRepository.deleteOldUrlsAndGetHash();
    }

    @Transactional
    public void loadingFreeHashFromDb(List<String> hashes) {
        log.info("Loading free hashes from db");
        hashRepository.saveBatch(hashes);
    }
}
