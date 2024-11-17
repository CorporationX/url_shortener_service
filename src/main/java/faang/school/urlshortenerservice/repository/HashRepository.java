package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :batchSize)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("batchSize") int batch);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM hash 
            WHERE hash IN (
                SELECT hash FROM hash 
                ORDER BY RANDOM() 
                LIMIT :batchSize
            )
            RETURNING hash;
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") long batchSize);
}
