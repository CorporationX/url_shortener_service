package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlJpaRepository extends JpaRepository<Url, String> {

    Url findByHash(String hash);

    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 year' RETURNING hash", nativeQuery = true)
    List<String> deleteExpiredUrlsReturningHashes();
}
