package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(value = """
            DELETE FROM your_table
            WHERE created_at < CURRENT_DATE - INTERVAL '1 year'
            RETURNING *;
            """, nativeQuery = true)
    List<Url> getAndDeleteYearOldObjects();
}
