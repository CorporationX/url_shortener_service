package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            INSERT INTO urls (hash, url)
            VALUES (?1, ?2)
            RETURNING *
            """)
    Url create(String hash, String originalUrl);

    List<Url> findAllByCreatedAtBefore(LocalDateTime dateTime);

    Optional<Url> findByHash(String hash);

    Optional<Url> findByOriginalUrl(String originalUrl);


}
