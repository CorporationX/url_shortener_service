package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url WHERE hash IN (
                SELECT hash FROM url WHERE created_at < :oldUrlCreationDateTime
                        ) RETURNING *
        """)
    List<Url> getAndDeleteOldHashes(@Param("oldUrlCreationDateTime") LocalDateTime oldUrlCreationDateTime);
}
