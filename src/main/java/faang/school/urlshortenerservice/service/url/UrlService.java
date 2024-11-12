package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.cache.HashCache;
import faang.school.urlshortenerservice.config.url.UrlProperties;
import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.dto.url.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlProperties urlProperties;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public List<Url> findAndReturnExpiredUrls() {
        return urlRepository.findAndReturnExpiredUrls();
    }

    @Transactional
    public ResponseUrlBody convertUrlToShort(RequestUrlBody requestUrlBody) {
        log.info("convertUrlToShort start, request - {}", requestUrlBody);
        String fullLink = requestUrlBody.getUrl();

        Url url = findUrlInDatabaseByFullUrl(fullLink)
                .orElseGet(() -> createNewUrlByFullLink(fullLink));

        ResponseUrlBody response = urlMapper.toResponseBody(url, urlProperties.getShortName());
        log.info("convertUrlToShort finish, response - {}", response);
        return response;
    }

    @Transactional
    public String getFullRedirectionLink(String hash) {
        log.info("convertShortLinkToFullLink start, hash - {}", hash);

        Url url = findUrlInCacheByHash(hash)
                .or(() -> findUrlInDatabaseByHash(hash))
                .orElseThrow(() -> new EntityNotFoundException("Url with hash " + hash + " not found"));

        log.info("convertShortLinkToFullLink finish, link - {}", url.getUrl());
        return url.getUrl();
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

    public Optional<Url> findUrlInCacheByHash(String hash) {
        log.debug("Trying to get url from cache by hash - {}", hash);
        return urlCacheRepository.findUrlInCacheByHash(hash);
    }
}
