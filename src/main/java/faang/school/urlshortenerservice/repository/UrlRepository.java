package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value = """
            SELECT hash FROM url WHERE created_at < NOW() - INTERVAL '1 year'
            """)
    Set<String> findOldUrls();

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE hash IN :hashes
            """)
    void deleteOldUrls(Set<String> hashes);
}
