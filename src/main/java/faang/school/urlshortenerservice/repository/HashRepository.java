package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :amount)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("amount") long amount);

    @Query(value = """
        DELETE FROM hash
        WHERE hash IN (
            SELECT hash
            FROM hash
            ORDER BY random()
            LIMIT :amount
            )
        RETURNING hash
        """, nativeQuery = true)
    List<String> getHashBatch(@Param("amount") long amount);
}
