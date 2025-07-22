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
    // TODO: Ну а если их миллион и несколько инстансов по крону одновременно это начнут делать
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < :minDate
            RETURNING *
            """)
    List<Url> deleteExpiredAndReturnHashes(@Param("minDate") LocalDateTime minDate);
}
