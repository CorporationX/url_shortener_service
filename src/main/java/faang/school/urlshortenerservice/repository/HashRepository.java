package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = "SELECT NEXTVAL('unique_number_seq') from generate_series(1, :sequenceNumberAmount)", nativeQuery = true)
    List<Long> getUniqueNumbersFromSequence(@Param("sequenceNumberAmount") Long sequenceNumberAmount);

    @Modifying
    @Query(nativeQuery = true, value = """
                    DELETE FROM hash h
                    USING (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :batchSize) AS rows
                    WHERE h.hash = rows.hash
                    RETURNING h.*
            """)
    List<Hash> getHashes(@Param("batchSize") int batchSize);
}