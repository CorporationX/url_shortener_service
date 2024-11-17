package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT NEXTVAL('unique_number_seq')
            FROM generate_series(1, :batchSize)
            """)
    List<Long> getUniqueNumbers(long batchSize);

    @Query(nativeQuery = true, value = """
                    DELETE FROM hash
                    WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY random()
                    LIMIT :amount
                    FOR UPDATE SKIP LOCKED
                    ) RETURNING hash
            """)
    List<String> findAndDelete(long amount);
}
