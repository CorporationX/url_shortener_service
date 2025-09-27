package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query( value = """
            SELECT nextval('your_sequence_name') FROM generate_series(1, N)
            """)
    List<Long> getUniqueNumbers(long count);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash = (
                SELECT hash FROM hash
                ORDER BY RANDOM()
                LIMIT ?)
            RETURNING hash
            """)
    @Modifying
    List<Hash> getHashBatch(long batchSize);
}
