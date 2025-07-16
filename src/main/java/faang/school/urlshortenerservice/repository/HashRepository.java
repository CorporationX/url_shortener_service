package faang.school.urlshortenerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import faang.school.urlshortenerservice.model.Hash;

public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n);", nativeQuery = true)
    List<Long> getNextOrderNumber(int n);

    @Modifying
    @Query(value = """
        WITH random_hashes AS (
            SELECT hash 
            FROM hash
            ORDER BY RANDOM()
            LIMIT :count
            FOR UPDATE
        )
        DELETE FROM hash
        WHERE hash IN (SELECT hash FROM random_hashes)
        RETURNING hash
        """, 
        nativeQuery = true)
    List<String> getHashBatch(int count);
}
