package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query
    @Transactional
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
        DELETE FROM url 
        WHERE created_at < NOW() - INTERVAL :expiration_interval
        RETURNING *;
    """)
    @Transactional
    List<Url> getAndDeleteExpiredUrl(@Param("expiration_interval") String interval);
}
