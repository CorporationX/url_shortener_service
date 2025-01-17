package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(nativeQuery = true, value = """
            WITH deleted_rows AS (
                DELETE FROM shortener_schema.url
                    WHERE created_at <= :createdAt
                    RETURNING *
                )
            SELECT * FROM deleted_rows
            """)
    List<Url> getOldUrls(@Param("createdAt") LocalDateTime createdAt);
}
