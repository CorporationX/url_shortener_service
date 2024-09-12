package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO urls (hash, url)
            VALUES (?1, ?2)
            RETURNING *
            """)
    Url create(String hash, String originalUrl);

    String getUrlByHash(String hash);
}
