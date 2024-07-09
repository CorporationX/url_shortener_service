package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)")
    List<Long> findUniqueSequence(Long max);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (
                SELECT hash FROM hash
                LIMIT ?
            )
            RETURNING *
            """)
    List<String> getHashBatch(Long batchSize);
}
