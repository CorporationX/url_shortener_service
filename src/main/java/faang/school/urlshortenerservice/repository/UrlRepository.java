package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Transactional
    @Modifying
    @Query(
            value = """
                    WITH deleted AS (
                        DELETE FROM url
                        WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 year'
                        RETURNING hash
                    )
                    INSERT INTO hash (hash)
                    SELECT hash FROM deleted
                    """,
            nativeQuery = true
    )
    void deleteOldUrlsAndRecycleHashes();
}
