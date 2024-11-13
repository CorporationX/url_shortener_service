package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < :createdAt
            RETURNING *
            """)
    List<Url> deleteOldUrls(LocalDateTime createdAt);
}
