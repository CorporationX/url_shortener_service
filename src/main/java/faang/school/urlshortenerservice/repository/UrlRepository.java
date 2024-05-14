package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query(nativeQuery = true, value = "DELETE FROM url WHERE hash IN (SELECT hash FROM url WHERE created_at < current_timestamp - interval '1 year') RETURNING *")
    List<Url> cleanOldHashes();
}
