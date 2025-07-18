package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.cache.HashCache;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRedisCache redisCache;
    private final UrlRepository urlRepository;

    @Transactional
    public String create(UrlDto urlDto) {
        String url = urlDto.url();
        String hash = hashCache.getHash().getHash();
        Url newUrl = Url.builder().hash(hash).url(url).createdAt(LocalDateTime.now()).build();
        urlRepository.save(newUrl);
        redisCache.save(hash, url);
        return hash;
    }

    public String find(String hash) {
        Example<Url> urlExample = Example.of(Url.builder().hash(hash).build());
        return Optional.ofNullable(redisCache.get(hash))
                .orElseGet(() ->
                        urlRepository.findAll(urlExample)
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Nonexistent hash"))
                                .getUrl()
                );
    }
}
