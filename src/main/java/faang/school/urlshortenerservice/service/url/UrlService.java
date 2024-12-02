package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.util.HashCache;
import faang.school.urlshortenerservice.util.uri.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UriBuilder uriBuilder;

    @Value("${spring.date.ttl.hour.url}")
    private long urlTtlInCache;

    @Transactional
    public String createHashUrl(String uri) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(uri)
                .build();

        urlRepository.save(url);
        urlCacheRepository.saveByTtlInHour(url, urlTtlInCache);

        return uriBuilder.response(hash);
    }

    @Transactional(readOnly = true)
    public String getPrimalUri(String hash) {
        Optional<Url> urlOpt = urlCacheRepository.findByHash(hash);
        if (urlOpt.isPresent()) {
            return urlOpt.get().getUrl();
        }
        Url url = urlRepository.findById(hash).orElseThrow(() ->
                new ResourceNotFoundException("Url by hash: %s not found", hash));

        urlCacheRepository.saveByTtlInHour(url, urlTtlInCache);

        return url.getUrl();
    }
}
