package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
            """)
    List<Long> getUniqueNumbers(int max);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY RANDOM()
                LIMIT ?
            )
            RETURNING *
            """)
    List<String> getHashBatch(int batchSize);
}
