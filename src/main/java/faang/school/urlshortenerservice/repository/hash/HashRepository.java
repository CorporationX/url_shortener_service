package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, CustomHashRepository {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :hashAmount)
            """)
    List<Long> getUniqueNumbers(int hashAmount);

    @Query(nativeQuery = true, value = """
            WITH random_rows AS (
                SELECT hash
                FROM hash
                ORDER BY RANDOM()
                LIMIT :hashAmount
            )
            DELETE FROM hash
            USING random_rows
            WHERE hash.hash = random_rows.hash
            RETURNING hash.hash;
            """)
    List<Hash> getHashBatch(int hashAmount);
}
