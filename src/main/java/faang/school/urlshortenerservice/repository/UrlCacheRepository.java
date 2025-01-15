package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;

    public void saveToCache(Url url){
    }

    public String getUrlByHash(String hash) {
        Url url = urlRepository.findByHash(hash).orElseThrow(
                () -> new IllegalArgumentException("Url not found")
        );
        return url.getUrl();
    }
}
