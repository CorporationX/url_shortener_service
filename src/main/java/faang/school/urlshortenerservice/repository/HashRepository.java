package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_sequence') FROM generate_series(1, :batchSize)",
            nativeQuery = true)
    List<Long> generateUniqueNumbers(@Param("hashBatchSize") Long batchSize);

    @Modifying
    @Query(value = """
            DELETE FROM url_hash
            WHERE hash IN (
                SELECT hash
                FROM url_hash
                LIMIT :batchSize
            )
            RETURNING hash;
            """, nativeQuery = true)
    List<Hash> popUrlHashes(@Param("hashBatchSize") Long batchSize);
}
