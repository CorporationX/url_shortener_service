package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.cache.HashCache;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${url.base-url}")
    private String baseUrl;

    private final HashCache hashCache;
    private final UrlRedisCache redisCache;
    private final UrlRepository urlRepository;

    @Transactional
    public String create(UrlDto urlDto) {
        String url = urlDto.url();
        String hash = hashCache.getHash().getHash();
        Url newUrl = Url.builder().hash(hash).url(url).build();
        urlRepository.save(newUrl);
        redisCache.save(hash, url);
        return baseUrl + hash;
    }

    public String find(String hash) {
        return Optional.ofNullable(redisCache.get(hash))
                .orElseGet(() ->
                        urlRepository.findByHash(hash)
                                .orElseThrow(() -> new IllegalArgumentException("Nonexistent hash"))
                                .getUrl()
                );
    }
}