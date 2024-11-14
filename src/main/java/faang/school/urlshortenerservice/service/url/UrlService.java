package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.dto.url.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${url.shortName}")
    private String urlShortPrefix;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public List<Url> findAndReturnExpiredUrls(int years) {
        return urlRepository.deleteAndReturnExpiredUrls(years);
    }

    @Transactional
    public ResponseUrlBody convertUrlToShort(RequestUrlBody requestUrlBody) {
        log.info("convertUrlToShort start, request - {}", requestUrlBody);
        String fullLink = requestUrlBody.getUrl();

        Url url = findUrlInDatabaseByFullUrl(fullLink)
                .orElseGet(() -> createNewUrlByFullLink(fullLink));

        ResponseUrlBody response = urlMapper.toResponseBody(url, urlShortPrefix);
        log.info("convertUrlToShort finish, response - {}", response);
        return response;
    }

    @Transactional
    public String getFullRedirectionLink(String hash) {
        log.info("convertShortLinkToFullLink start, hash - {}", hash);

        String fullLink = findUrlInCacheByHash(hash);

        if (fullLink == null || fullLink.isBlank()) {
            Url url = findUrlInDatabaseByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("Url with hash " + hash + " not found"));
            fullLink = url.getUrl();
        }

        log.info("convertShortLinkToFullLink finish, link - {}", fullLink);
        return fullLink;
    }

    @Transactional
    private Url createNewUrlByFullLink(String fullLink) {
        log.debug("createNewUrlByFullLink start, fullLink - {}", fullLink);
        Url url = Url.builder()
                .hash(hashCache.getHash())
                .url(fullLink)
                .build();

        log.debug("createNewUrlByFullLink trying to save url in database - {}", url);
        url = urlRepository.save(url);
        log.debug("createNewUrlByFullLink trying to save url in cache - {}", url);
        urlCacheRepository.save(url);
        return url;
    }

    @Transactional(readOnly = true)
    public Optional<Url> findUrlInDatabaseByFullUrl(String fullLink) {
        log.debug("Trying to get url from database by full url - {}", fullLink);
        return urlRepository.findByUrlIgnoreCase(fullLink);
    }

    @Transactional(readOnly = true)
    public Optional<Url> findUrlInDatabaseByHash(String hash) {
        log.debug("Trying to get url from database by hash - {}", hash);
        return urlRepository.findByHashIgnoreCase(hash);
    }

    public String findUrlInCacheByHash(String hash) {
        log.debug("Trying to get url from cache by hash - {}", hash);
        return urlCacheRepository.findUrlInCacheByHash(hash);
    }
}
