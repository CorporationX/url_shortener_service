package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = """
                     SELECT nextval('unique_number_seq')
                     FROM generate_series(1, :batchSize)
                     """)
    List<Long> getUniqueNumbers(@Param("batch") long batch);

    @Transactional
    @Query(nativeQuery = true,
            value = """
                     DELETE FROM hash
                     WHERE hash IN(
                     SELECT hash
                     FROM hash ORDER BY random()
                     LIMIT :batchSize)
                     RETURNING hash
                     """)
    List<Hash> getHashBatch(int batch);
}
