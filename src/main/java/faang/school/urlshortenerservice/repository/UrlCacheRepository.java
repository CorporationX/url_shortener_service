package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    public void save(Url url) {
        System.out.println("save in redis");
    }
}
