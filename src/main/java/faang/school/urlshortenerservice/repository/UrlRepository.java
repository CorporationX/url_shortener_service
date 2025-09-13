package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {

    @Query(value = """
    DELETE FROM url
        WHERE created_at < NOW() - (:olderThanDays * INTERVAL '1 day')
    RETURNING hash
    """, nativeQuery = true)
    List<String> getHashesOlderThanAndDelete(int olderThanDays);

    Optional<Url> findByHash(String hash);
}
