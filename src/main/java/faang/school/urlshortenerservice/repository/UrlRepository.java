package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
        DELETE FROM url 
        WHERE created_at < NOW() - INTERVAL '1 YEAR'
        RETURNING *;
    """)
    List<Url> deleteExpiredUrl();
}
