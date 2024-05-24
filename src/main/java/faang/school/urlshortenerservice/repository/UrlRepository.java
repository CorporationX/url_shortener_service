package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash
        """)
    Set<String> deleteOldUrlsAndReturnHashes();


    Url findByHash(String hash);
}
