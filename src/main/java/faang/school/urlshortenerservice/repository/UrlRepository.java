package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlBaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  UrlRepository extends JpaRepository<UrlBaza,String> {

    @Query(nativeQuery = true, value = """
            WITH deleted AS (
                DELETE FROM url
                WHERE createdAt < NOW() - INTERVAL '1 year'
                RETURNING hash
                )
                INSERT INTO hash (hash)
                SELECT hash FROM deleted;
            """)
    void deleteOldUrlsAndCreatedHashes();
}