package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(value = """
            DELETE FROM url u 
                   WHERE u.cache_date <= :date 
                   RETURNING u.hash
            """, nativeQuery = true)
    List<String> releaseUnusedHashesFrom(LocalDate date);
}
