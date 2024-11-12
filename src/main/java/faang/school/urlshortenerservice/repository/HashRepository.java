package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :batchSize);
            """)
    List<Long> getUniqueNumbers(@Param("batchSize") long batchSize);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (
                SELECT hash FROM hash ORDER BY random() LIMIT :batchSize
            )
            RETURNING hash;
            """)
    List<Hash> getAndDeleteHashBatch(@Param("batchSize") long batchSize);
}