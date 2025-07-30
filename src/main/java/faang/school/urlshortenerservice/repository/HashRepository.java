package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash 
            WHERE hash IN (
                SELECT hash 
                FROM hash
                ORDER BY RANDOM()
                FOR UPDATE
                SKIP LOCKED
                LIMIT :limit
            )
            RETURNING *;
            """)
    List<String> getHashBatch(int limit);
}
