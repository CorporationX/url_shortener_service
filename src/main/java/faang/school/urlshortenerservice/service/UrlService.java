package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.builder.UrlBuilder;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlBuilder urlBuilder;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String createHashUrl(String uri) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(uri)
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);

        return urlBuilder.makeUrl(url.getHash());
    }
}
