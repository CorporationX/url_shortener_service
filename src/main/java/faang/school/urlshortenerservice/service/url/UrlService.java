package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.util.HashCache;
import faang.school.urlshortenerservice.util.url.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UriBuilder uriBuilder;

    @Transactional
    public String createHashUrl(String uri) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(uri)
                .build();

        urlCacheRepository.save(url);
        urlRepository.save(url);

        return uriBuilder.response(hash);
    }

    @Transactional(readOnly = true)
    public String getPrimalUri(String hash) {
        Url url = urlCacheRepository.findByHash(hash);
        if (url == null) {
            url = urlRepository.findById(hash).orElseThrow(() ->
                    new RuntimeException("Url by hash: {} not found" + hash));
            urlCacheRepository.save(url);
        }
        return url.getUrl();
    }
}
