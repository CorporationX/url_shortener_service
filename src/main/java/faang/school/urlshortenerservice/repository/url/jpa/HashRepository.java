package faang.school.urlshortenerservice.repository.url.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<String, String> {

    @Query(
            value = "SELECT NEXTVAL('unique_number_seq') FROM generate_series(1, ?1)",
            nativeQuery = true
    )
    List<Long> getUniqueNumbers(int batchSize);

    @Query(
            value = """
                    DELETE FROM hash h
                        WHERE h.hash IN (
                        SELECT h.hash
                        FROM hash h
                        ORDER BY h.hash
                        LIMIT ?1
                        )
                    RETURNING *;
                    """,
            nativeQuery = true
    )
    List<String> getHashBatch(int batchSize);
}
