package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(value = """
            DELETE FROM url u
                   WHERE u.last_ttl_expiration_date <= :date
                   RETURNING u.hash
            """, nativeQuery = true)
    List<String> releaseUnusedHashesFrom(LocalDate date);

    Optional<Url> findUrlByUrl(String url);
}
