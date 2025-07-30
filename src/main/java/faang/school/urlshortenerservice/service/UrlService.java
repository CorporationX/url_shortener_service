package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.storage.HashInMemoryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashInMemoryCache hashInMemoryCache;
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
        // todo add to cache
        return savedUrl;
    }

}
