package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = """
            WITH updated AS (
                UPDATE unique_number_seq
                SET sequence = sequence + :count
                RETURNING sequence - :count AS start_value, sequence AS end_value
            )
            SELECT generate_series(updated.start_value, updated.end_value - 1) AS value
            FROM updated
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("count") int count);

    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY hash
                LIMIT :count
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("count") int count);
}
