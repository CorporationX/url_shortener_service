package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCache {

    private final UrlRepository urlRepository;

    @Cacheable(value = "url", key = "#hash")
    public String getUrlByHash(String hash) {
        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(
                        String.format("URL для хеша: %s не найден", hash)));
    }

    @CachePut(value = "url", key = "#url.hash")
    public void saveUrlByHash(Url url) {
        urlRepository.save(url);
    }
}
