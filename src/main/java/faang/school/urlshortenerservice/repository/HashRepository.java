package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Query(nativeQuery = true, value = """
                DELETE FROM hash AS h WHERE hash IN (SELECT hash FROM hash AS h LIMIT :amount)
                RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
