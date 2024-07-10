package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Override
    @Transactional
    public void createUrlHash(URL url) {

        String hash = hashCache.pop();

        Url entity = Url.builder()
                .hash(hash)
                .url(url)
                .build();

        entity = urlRepository.save(entity);
        urlCacheRepository.saveUrlByHash(hash, url.toString());

        log.info("Saved new URL mapping: {}", entity);
    }

    @Override
    @Transactional(readOnly = true)
    public String getUrlFromHash(String hash) {

         Optional<String> cacheUrl = urlCacheRepository.getUrlByHash(hash);

         if (cacheUrl.isPresent()) {
             return cacheUrl.get();
         }

        Url entityUrl = urlRepository.findById(hash)
                .orElseThrow(() -> new NotFoundException("Url with hash=" + hash + " not found"));

         return entityUrl.getHash();
    }
}
