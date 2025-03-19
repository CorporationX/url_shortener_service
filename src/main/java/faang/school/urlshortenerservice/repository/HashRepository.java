package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(@Param("maxRange") int maxRange);

    @Query(value = """
            DELETE FROM hash 
            WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :batchSize) 
            RETURNING hash
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
