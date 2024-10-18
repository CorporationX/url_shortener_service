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

  @Query(nativeQuery = true, value = """
          WITH deleted AS (
              DELETE FROM url
              WHERE created_at < :dateExpired
              RETURNING hash
          )
          SELECT hash FROM deleted
          """)
  @Modifying
  List<String> findAndDeleteHashExpired(@Param("dateExpired") LocalDateTime dateExpired);

  Optional<Url> findByHash(String hash);
}
