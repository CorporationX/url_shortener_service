package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlsRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(value = """
            INSERT INTO urls (hash, url, created_at)
            VALUES (?1, ?2, NOW())
            """, nativeQuery = true)
    void save(String hash, String url);

    @Query(value = """
             DELETE FROM urls WHERE created_at < (now() - INTERVAL ':period second') returning hash
            """, nativeQuery = true)
    List<String> deleteExpired(@Param("period") Long period);
}
