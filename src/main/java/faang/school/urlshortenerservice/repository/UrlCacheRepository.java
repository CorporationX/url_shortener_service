package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {
    public String getHashFromCache(String url) {
        return url;
    }

    public void saveToCache(Url url){
    }
}
