package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.dto.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)", nativeQuery = true)
    List<Long> getUniqueNumber(@Param("maxRange") int maxRange);

    @Query(value = """
            WITH deleted AS (DELETE FROM hash
                WHERE ctid IN (SELECT ctid FROM hash LIMIT :batchSize)
                RETURNING *
            )
            SELECT hash FROM deleted;""", nativeQuery = true)
    List<String> getHashBatch(@Param("batchSize") long batchSize);

}
