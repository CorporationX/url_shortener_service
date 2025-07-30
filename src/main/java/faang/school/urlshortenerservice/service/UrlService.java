package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.redis.UrlRedisCacheService;
import faang.school.urlshortenerservice.storage.HashInMemoryCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static faang.school.urlshortenerservice.utils.ConstantsUtilClass.NO_MAPPING_URL_FOR_HASH;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashInMemoryCache hashInMemoryCache;
    private final UrlRedisCacheService urlRedisCacheService;
    @Value("${app.url.expire-period-year}")
    private int expirePeriodInYear;

    public Url createUrlMapping(String url, LocalDateTime expireAt) {
        Url urlMapping = new Url();
        urlMapping.setUrl(url);
        LocalDateTime actualExpireAt = LocalDateTime.now().plusYears(expirePeriodInYear);
        if (expireAt!=null && actualExpireAt.isAfter(expireAt)) {
            actualExpireAt = expireAt;
        }
        urlMapping.setExpireAt(actualExpireAt);
        urlMapping.setHash(hashInMemoryCache.getHash());
        Url savedUrl = urlRepository.save(urlMapping);
        urlRedisCacheService.saveUrlMapping(savedUrl.getHash(), savedUrl.getUrl());
        return savedUrl;
    }

    public URI getActualUrl(String hash) {
        Optional<String> optUrlValue = urlRedisCacheService.getActualUrl(hash);
        if (optUrlValue.isPresent()) {
            return URI.create(optUrlValue.get());
        } else {
            Optional<Url> optDBUrlValue = urlRepository.findByHash(hash);
            if (optDBUrlValue.isPresent()) {
                String actualUrl = optDBUrlValue.get().getUrl();
                urlRedisCacheService.saveUrlMapping(hash, actualUrl);
                return URI.create(actualUrl);
            } else {
                log.error(String.format(NO_MAPPING_URL_FOR_HASH, hash));
                throw new EntityNotFoundException(String.format(NO_MAPPING_URL_FOR_HASH, hash));
            }
        }
    }
}
