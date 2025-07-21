package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = """
    WITH old AS (
      SELECT hash
      FROM url
      WHERE created_at < (NOW() - :ageIntervalSql::INTERVAL)
      LIMIT :batchSize
      FOR UPDATE SKIP LOCKED
    )
    DELETE FROM url u
    USING old o
    WHERE u.hash = o.hash
    RETURNING o.hash
    """, nativeQuery = true)
    List<String> deleteOldReturningHashes(
            @Param("ageIntervalSql") String ageIntervalSql,
            @Param("batchSize") int batchSize
    );
}
