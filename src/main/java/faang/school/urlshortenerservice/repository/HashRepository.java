package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<String, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange))
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Query(nativeQuery = true, value = """
            DELETE FROM hashes WHERE hash IN (
            SELECT hash FROM hashes ORDER BY hash ASC LIMIT :amount
            ) RETURNING *
            """)
    List<String> getHashBatch(long amount);
}
