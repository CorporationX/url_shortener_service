package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
    SELECT nextval('unique_number_seq') 
    FROM generate_series(1, :range)
    """)
    List<Long> getUniqueNumbers(int range);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash_value IN (
                SELECT hash_value FROM hash
                ORDER BY random()
                LIMIT :amount
                FOR UPDATE SKIP LOCKED
            )
            RETURNING hash_value
            """)
    List<String> getHashBatch(int amount);

}