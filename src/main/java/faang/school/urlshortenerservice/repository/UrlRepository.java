package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

   Optional<String> findByHash(String hash);

   @Modifying
   @Transactional
   @Query(value = "WITH deleted AS (" +
           "    DELETE FROM url " +
           "    WHERE expired_at < now() " +
           "    RETURNING hash" +
           ") " +
           "SELECT hash FROM deleted", nativeQuery = true)
   List<String> deleteExpiredUrlsAndReturnHashes();
}
