package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    public String createShortUrl(String url) {
        String hash = hashCache.getHash();

        Url newUrl = Url.builder()
                .hash(hash)
                .url(url)
                .build();

        urlRepository.save(newUrl);
        log.info("Url = {} save in bd, hash = {}", url, hash);
        return hash;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "${redis.cacheable}")
    public String getLongUrl(String hash) {
        return urlRepository.getUrl(hash).orElseThrow(() ->
                new IllegalArgumentException("Not valid hash"));
    }
}
