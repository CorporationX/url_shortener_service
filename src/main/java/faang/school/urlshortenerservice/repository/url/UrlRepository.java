package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.model.url.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends CrudRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < :oldest
            RETURNING hash
            """)
    List<String> removeOld(LocalDateTime oldest);
}
