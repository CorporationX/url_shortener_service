package faang.school.urlshortenerservice.service.urlService;

import faang.school.urlshortenerservice.config.url.UrlConfig;
import faang.school.urlshortenerservice.dto.RequestUrlBody;
import faang.school.urlshortenerservice.dto.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hashCache.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlConfig urlConfig;


    @Transactional
    public ResponseUrlBody convertLink(RequestUrlBody requestUrlBody) {
        log.info("createShortLink start, request - {}", requestUrlBody);
        String fullLink = requestUrlBody.getUrl();

        Url url = findUrlInDatabaseByFullUrl(fullLink).orElseGet(() -> createNewLink(fullLink));

        ResponseUrlBody response = urlMapper.toResponseBody(url, urlConfig.getShortName());
        log.info("convertLink finish, response - {}", response);
        return response;
    }

    @Transactional
    public String redirectLink(String hash) {
        log.info("redirectLink start, hash - {}", hash);

        String fullLink = findUrlInCacheByHash(hash);

        if (fullLink == null || fullLink.isBlank()) {
            Url url = findUrlInDatabaseByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("Url with hash " + hash + " not found"));
            fullLink = url.getUrl();
        }

        log.info("redirectLink finish, link - {}", fullLink);
        return fullLink;
    }

    @Transactional
    public List<Url> findAndReturnExpiredUrls(int years) {
        return urlRepository.deleteAndReturnExpiredUrls(years);
    }

    @Transactional
    private Url createNewLink(String fullLink) {
        log.info("createNewLink start");

        Url url = Url.builder()
                .hash(hashCache.getHash())
                .url(fullLink)
                .build();
        log.debug("create new url: {}", url);

        url = urlRepository.save(url);
        log.debug("successful saving url in database - {}", url);

        urlCacheRepository.save(url);
        log.debug("successful saving url in cache - {}. createNewLink finis", url);
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
