package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.enity.FreeHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeHashRepository extends JpaRepository<FreeHash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :count)
            """)
    List<Long> getListSequence(@Param(value = "count") int count);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_hash
            WHERE id IN (
                SELECT id FROM free_hash
                LIMIT :count
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash;
            """)
    List<String> getAndDeleteHashes(@Param("count") int count);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) FROM free_hash
            """)
    Long getCountHashes();
}
