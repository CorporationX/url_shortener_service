package faang.school.urlshortenerservice.repository;

import org.hibernate.validator.constraints.URL;
import org.springframework.stereotype.Component;

@Component
public interface UrlCacheRepository {
    void save(URL url);
}