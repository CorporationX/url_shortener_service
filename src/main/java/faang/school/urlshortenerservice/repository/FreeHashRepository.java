package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.FreeHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeHashRepository extends JpaRepository<FreeHash, String> {

    @Query(nativeQuery = true, value = """
                SELECT nextval('hash_sequence')
                FROM generate_series(1, :maxRange)
            """)
    List<Long> generateBatch(long maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
    DELETE FROM free_hashes
    WHERE ctid IN (
        SELECT ctid
        FROM free_hashes
        ORDER BY RANDOM()
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    )
    RETURNING *
    """)
    List<FreeHash> deleteAndReturnFreeHashes(@Param("limit") int limit);

    @Query(value = "SELECT pg_try_advisory_xact_lock(:lockKey)", nativeQuery = true)
    boolean tryAdvisoryLock(@Param("lockKey") long lockKey);
}
