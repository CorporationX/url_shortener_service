package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UrlRepository {

    public Url save(Url url) {
        url.setCreatedAt(LocalDateTime.now());
        return url;
    }
}
