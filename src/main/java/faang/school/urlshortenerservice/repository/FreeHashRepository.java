package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.FreeHash;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query(nativeQuery = true, value = """
            SELECT * FROM free_hashes
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """)
    List<FreeHash> findAndLockFreeHashes(@Param("limit") int limit);
}
