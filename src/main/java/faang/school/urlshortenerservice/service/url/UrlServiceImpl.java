package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Override
    @Transactional
    public void createShorUrl(URL url) {

        String hash = hashCache.pop();

        Url entity = Url.builder()
                .hash(hash)
                .url(url)
                .build();

        entity = urlRepository.save(entity);
        urlCacheRepository.saveUrlByHash(url.toString(), hash);

        log.info("Saved new URL mapping: {}", entity);
    }
}
