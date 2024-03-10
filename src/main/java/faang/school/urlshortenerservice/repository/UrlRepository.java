package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    Url findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE createdAt < NOW() - INTERVAL '1 year' RETURNING hash
            """)
    List<String> deleteOlderThanYear();
}
