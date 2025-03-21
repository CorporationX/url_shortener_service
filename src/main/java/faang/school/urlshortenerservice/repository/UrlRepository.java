package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url h 
            WHERE (
            SELECT hash FROM url h
            WHERE h.created_at > :maxTimeClear
            )
            RETURNING hash
            """)
    List<String> deleteRecordsAndReturnHash(LocalDateTime maxTimeClear);

    @Query(nativeQuery = true, value = """
                SELECT url 
                FROM url 
                WHERE hash = :hash
            """)
    Optional<String> findUrlByHash(@Param("hash") String hash);
}