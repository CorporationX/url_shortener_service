package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Object, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Query(value = "INSERT INTO hash (hash) SELECT unnest(:hashes)", nativeQuery = true)
    void saveHashes(@Param("hashes") String[] hashes);

    @Modifying
    @Query(value = "WITH deleted AS (" +
            "    DELETE FROM hash " +
            "    WHERE id IN (SELECT id FROM hash ORDER BY random() LIMIT :batchSize FOR UPDATE) " +
            "    RETURNING hash" +
            ") " +
            "SELECT hash FROM deleted", nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
