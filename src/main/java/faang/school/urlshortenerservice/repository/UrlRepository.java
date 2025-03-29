package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UrlRepository extends CrudRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT u.hash FROM url u WHERE u.url = :url
            """)
    String hashForUrlIfExists(String url);

    @Query(nativeQuery = true,
            value = "DELETE FROM url WHERE " +
                    "created_at < NOW() - INTERVAL '1 year' " +
                    "RETURNING hash")
    List<String> removeExpiredUrls();
}