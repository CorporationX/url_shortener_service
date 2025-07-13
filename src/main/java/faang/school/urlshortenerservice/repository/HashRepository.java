package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
             SELECT nextval('unique_hash_number_sequence') AS generated_value FROM generate_series(1,:maxRange);
            """)
    List<Long> getNextRange(int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
                DELETE FROM hash WHERE id IN (SELECT id FROM hash order by id ASC LIMIT :amount) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
