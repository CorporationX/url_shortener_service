package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortLink(UrlDto urlDto) {
        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.url())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(url.getUrl(), hash);

        log.info("Hash {} for URL {} has been created", hash, url);
        return hash;
    }

    public String getUrl(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.findUrlByHash(hash);
        if (cachedUrl.isPresent()) {
            String url = cachedUrl.get();
            log.info("URL {} is obtained from Redis", url);
            return url;
        }

        String urlFromDb = urlRepository.getUrl(hash);
        if (urlFromDb != null) {
            log.info("URL {} is obtained from the database", urlFromDb);
            return urlFromDb;
        }
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}
