package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :cutoffDate
        RETURNING hash
        """)
    List<String> deleteOldUrlsAndReturnHashes(LocalDateTime cutoffDate);


    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    String findHashByUrl(@Param("url") String url);
}
