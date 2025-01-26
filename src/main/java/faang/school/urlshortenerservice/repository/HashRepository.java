package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
        SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :quantity)
        """)
    List<Long> getUniqueNumbers(int quantity);

    @Query(nativeQuery = true, value = """
        DELETE FROM hash WHERE hash IN (
            SELECT hash FROM hash ORDER BY hash ASC LIMIT :batchSize
        ) RETURNING hash
        """)
    List<Hash> getHashBatch(int batchSize);
}
