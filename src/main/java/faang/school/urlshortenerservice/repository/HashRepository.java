package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') AS generated_value FROM generate_series(1, :maxRange)
            """)
    List<Long> getNextRange(int maxRange);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN(
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount FOR UPDATE
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
