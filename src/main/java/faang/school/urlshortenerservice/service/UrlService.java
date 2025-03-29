package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlProperties;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${spring.base_url}")
    private String baseUrl;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlProperties urlProperties;



    @Transactional
    public String createShortUrl(String url) {
        String hash = hashCache.getHash();

        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .deletedAt(LocalDateTime.now().plus(urlProperties.getExpirationPeriod()))
                .build();

        urlRepository.save(urlEntity);

        urlCacheRepository.set(hash, url);

        return buildShortUrl(hash);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {

            return cachedUrl;
        }

        Url url = urlRepository.findById(hash)
                .orElseThrow(()-> new UrlNotFoundException("URL not found for hash: " + hash));

        return url.getUrl();
    }

    private String buildShortUrl(String hash) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(hash)
                .build()
                .toUriString();
    }
}
