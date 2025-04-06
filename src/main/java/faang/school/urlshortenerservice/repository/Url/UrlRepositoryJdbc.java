package faang.school.urlshortenerservice.repository.Url;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepositoryJdbc {
    void saveUrlsBatch(List<Url> urls);
}
