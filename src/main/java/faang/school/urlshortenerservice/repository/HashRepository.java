package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
            SELECT nextval('unique_number_seq') from generate_series(1, :amount)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("amount") int amount);

    @Modifying
    @Query(value = """
        DELETE FROM hash
        WHERE hash IN (
            SELECT h.hash FROM hash h
            LEFT JOIN url u ON h.hash = u.hash
            WHERE u.hash IS NULL
            ORDER BY h.hash LIMIT :batchSize
        )
        RETURNING *
        """, nativeQuery = true)
    List<Hash> getHashBatch(@Param("batchSize") int batchSize);
}
