package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

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

    //тут вопрос если одна и та же ссылка приходит мы что делает ? просто возвращаем из бд или новый hash ?
    @Transactional
    public URL createNewShortUrl(URL url) {
        log.info("calling localCache to get hash");
        String hash = localCache.getCache();

        Url entityUrl = new Url();
        entityUrl.setHash(hash);
        entityUrl.setUrl(url);

        log.info("saving hash and url at db and redis");
        urlRepository.save(entityUrl);
        urlCacheRepository.saveAtRedis(entityUrl);

        return createShortURLWithHash(hash);
    }

    public URL getUrl(String hash) {
        log.info("trying to find url at redis");
        URL result = urlCacheRepository.getFromRedis(hash);

        if (result != null) {
            return result;
        }

        try {
            log.info("trying to find url at db");
            Url url = urlRepository.findByHash(hash);
            return url.getUrl();
        } catch (Exception e) {
            log.error("cant fiend url at redis and db", e);
            throw new IllegalArgumentException("can't redirect to main ulr , incorrect hash", e);
        }
    }

    private URL createShortURLWithHash(String hash) {
        String fullUrl = link.concat(hash);
        try {
            log.info("trying to create new short url");
            return new URL(fullUrl);
        } catch (MalformedURLException e) {
            log.error("cant create url with given link and hash, check properties");
            throw new IllegalArgumentException("Something went wrong when creating shortUrl", e);
        }
    }
}
