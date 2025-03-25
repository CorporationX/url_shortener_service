package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < ?1
        RETURNING *;
    """)
    List<Url> pollOldUrls(LocalDateTime createdAt);

    @Query(nativeQuery = true, value = """
        SELECT * FROM url
        WHERE hash = ?1 
        LIMIT 1;
    """)
    Optional<Url> findByHash(String hash);

}
