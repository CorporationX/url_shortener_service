package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM url WHERE hash IN (SELECT hash FROM url WHERE created_at < current_timestamp - interval '1 year') RETURNING *")
    List<Url> cleanOldHashes();
}
