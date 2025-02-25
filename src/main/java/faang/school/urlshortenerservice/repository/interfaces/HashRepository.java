package faang.school.urlshortenerservice.repository.interfaces;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('url_unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
             DELETE FROM url_hashes url_hashes WHERE url_hashes.hash IN (
                SELECT hash FROM url_hashes LIMIT (:hashRange)
                ) 
             RETURNING *
            """)
    List<Hash> getHashesAndDelete(@Param("hashRange") long hashRange);


    @Modifying
    @Query(nativeQuery = true, value = """
            WITH selected_rows as (
                DELETE FROM url_urls
                       WHERE created_at < NOW() - INTERVAL '1 year'
                RETURNING hash
            )
            INSERT INTO url_hashes (hash)
                        SELECT hash FROM selected_rows
            """)
    void cleanDataOlder1Year();
}