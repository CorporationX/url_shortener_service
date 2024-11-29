package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Transactional
    @Modifying
    @Query(value = """
            WITH cte AS (
                SELECT hash
                FROM hash
                ORDER BY random()
                LIMIT :number
                FOR UPDATE SKIP LOCKED
            )
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM cte)
            RETURNING hash
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("number") int number);
}