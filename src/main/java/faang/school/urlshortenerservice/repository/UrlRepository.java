package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    @Query(nativeQuery = true, value =
            """
                DELETE FROM url
                WHERE created_at < NOW() - INTERVAL :interval
                RETURNING hash
            """)
    List<String> deleteUrl(String interval);

}
