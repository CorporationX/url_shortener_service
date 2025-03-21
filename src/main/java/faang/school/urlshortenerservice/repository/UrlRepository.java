package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < now() - make_interval(days => :daysAgo)
            RETURNING hash;
            """)
    List<String> clearOldHashes(int daysAgo);

    @Query(value = """
            SELECT u.url FROM Url u
            WHERE u.hash = :hash
            """)
    Optional<String> getByHash(String hash);
}