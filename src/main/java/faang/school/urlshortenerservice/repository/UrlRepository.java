package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at < NOW() - CAST(:interval AS INTERVAL) RETURNING hash""")
    List<Hash> deleteOutdatedUrlsAndReturnHashes(String interval);
}
