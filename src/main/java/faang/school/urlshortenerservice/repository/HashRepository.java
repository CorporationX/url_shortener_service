package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_sequence') FROM generate_series(1, :batchSize)",
            nativeQuery = true)
    List<Long> generateUniqueNumbers(@Param("batchSize") Long batchSize);

    @Modifying
    @Query(value = """
            DELETE FROM url_hashes
            WHERE hash IN (
                SELECT hash
                FROM url_hashes
                LIMIT :batchSize
            )
            RETURNING hash;
            """, nativeQuery = true)
    List<Hash> popUrlHashes(@Param("batchSize") double batchSize);

    @Modifying
    @Query(value = """
            DELETE FROM urls
            WHERE created_at < :dateTime
            RETURNING urls.hash
            """, nativeQuery = true)
    List<Hash> deleteHashesLaterThan(@Param("dateTime") LocalDateTime dateTime);
}
