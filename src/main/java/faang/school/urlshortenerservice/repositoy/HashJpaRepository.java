package faang.school.urlshortenerservice.repositoy;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashJpaRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n )", nativeQuery = true)
    List<Long> getUniqueNumbers(long n);

    @Modifying
    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY hash
                LIMIT :n
            )
            RETURNING hash
            """, nativeQuery = true)
    List<Hash> getHashBatch(long n);
}
