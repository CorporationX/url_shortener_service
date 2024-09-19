package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, String> {

    @Query(nativeQuery = true, value = """
              DELETE FROM url
              WHERE created_at < NOW() - INTERVAL '1 year'
              RETURNING *
            """)
    @Modifying
    List<Url> clearOlderThanYear();

    boolean existsByUrl(String url);

    Url findByUrl(String url);

    Optional<Url> findById(String hash);
}
