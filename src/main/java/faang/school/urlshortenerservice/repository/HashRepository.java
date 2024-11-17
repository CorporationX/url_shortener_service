package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM hash\n" +
            "WHERE hash IN (\n" +
            "    SELECT hash\n" +
            "    FROM hash\n" +
            "    ORDER BY random()\n" +
            "    LIMIT :number\n" +
            "    FOR UPDATE SKIP LOCKED\n" +
            ")\n" +
            "RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(@Param("number") int number);
}