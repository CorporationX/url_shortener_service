package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findUrlByHash(String hash);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            DELETE FROM urls
            WHERE created_at < NOW() - INTERVAL :years YEAR
            RETURNING hash
            """)
    List<String> getHashesAndDeleteExpiredUrls(@Param("years") int years);
}
