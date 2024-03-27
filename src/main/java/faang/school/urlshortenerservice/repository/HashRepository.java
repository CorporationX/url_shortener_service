package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :maxRange)         
            """)
    List<Long> getNextBatch(int batchSize);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *
            """)
    List<Hash> findAndDelete(int amount);
}
