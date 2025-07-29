package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM urls
            WHERE created_at <  current_date - make_interval(days => :daysCount)
            RETURNING hash
            """, nativeQuery = true)
    List<String> reuseOldUrls(@Param("daysCount")int daysCount);

    Optional<Url> findByUrl(String url);
}
