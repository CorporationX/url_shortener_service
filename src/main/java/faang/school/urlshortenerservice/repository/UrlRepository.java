package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.CustomUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<CustomUrl, String> {

    @Query("""
                SELECT cu.url
                FROM CustomUrl cu
                WHERE cu.hash=:hash
            """)
    String findUrlByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url u
            WHERE (
                SELECT hash
                FROM url u
                WHERE u.created_at > :maxAvailableTime
            )
            RETURNING hash
            """)
    List<String> findHashesWithExpiredDates(LocalDateTime maxAvailableTime);
}
