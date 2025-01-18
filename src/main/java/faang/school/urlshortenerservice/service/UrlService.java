package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlServiceValidator urlServiceValidator;
    private final LocalCache localCache;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${link}")
    private String link;

    public URL createNewShortUrl(URL url) {
        Optional<Url> urlFromDb = findByUrl(url);
        if (urlFromDb.isPresent()) {
            return createShortURLWithHash(urlFromDb.get().getHash());
        }

        log.info("calling localCache to get hash");
        String hash = localCache.getCache();

        Url entityUrl = new Url();
        entityUrl.setHash(hash);
        entityUrl.setUrl(url);

        log.info("saving hash and url at db and redis");
        urlRepository.save(entityUrl);
        urlCacheRepository.saveAtRedis(hash,url);

        return createShortURLWithHash(hash);
    }

    @Cacheable(value = "url", key = "#hash", cacheManager = "cacheManager")
    public URL getUrl(String hash) {
        log.info("validate hash");
        urlServiceValidator.validateHash(hash);

        log.info("trying to find url at db");
        return findByHash(hash).getUrl();
    }

    private Optional<Url> findByUrl(URL url) {
        log.info("getting optional url from db");
        return urlRepository.findByUrl(url);
    }

    private Url findByHash(String hash){
       return urlRepository.findByHash(hash).orElseThrow(
                ()->{
                    log.error("cant fiend url at redis and db with given hash:" + hash);
                    return new EntityNotFoundException("can't redirect to main ulr , incorrect hash: " + hash);
                });
    }


    private URL createShortURLWithHash(String hash) {
        String fullUrl = link.concat(hash);
        try {
            log.info("trying to create new short url");
            return new URL(fullUrl);
        } catch (MalformedURLException e) {
            log.error("cant create url with given link and hash, check properties. hash: " + hash, e);
            throw new InvalidUrlException("Something went wrong when creating shortUrl");
        }
    }
}
