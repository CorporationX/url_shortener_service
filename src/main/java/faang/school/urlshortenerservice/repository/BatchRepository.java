package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BatchRepository extends JpaRepository<Hash, String>, CustomBatchRepository<Hash> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') AS generated_value FROM generate_series(1, :maxRange)
            """)
    List<Long> getNextRange(int maxRange);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash.hash IN(
                SELECT hash.hash FROM hash ORDER BY hash.hash ASC LIMIT :amount FOR UPDATE
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
