package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT u.hash FROM Url u WHERE u.url = :url
            """)
    String hashForUrlIfExists(String url);
}
