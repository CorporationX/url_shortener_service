package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, CustomHashRepository {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :hashAmount)
            """)
    List<Long> getUniqueNumbers(int hashAmount);

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH to_delete AS (
                SELECT hash
                FROM hash
                ORDER BY hash
                LIMIT :hashAmount
            )
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM to_delete)
            RETURNING hash
            """)
    List<Hash> getHashBatch(int hashAmount);
}
