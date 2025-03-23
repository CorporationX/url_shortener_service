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
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Transactional
    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO hash (hash) SELECT unnest(:hashes)", nativeQuery = true)
    void saveHashes(@Param("hashes") String[] hashes);

    @Transactional
    @Modifying
    @Query(value = "WITH deleted AS (" +
            "    DELETE FROM hash " +
            "    WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT :batchSize FOR UPDATE SKIP LOCKED) " +
            "    RETURNING hash" +
            ") " +
            "SELECT hash FROM deleted", nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
