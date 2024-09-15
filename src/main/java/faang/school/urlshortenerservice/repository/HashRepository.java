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

    @Query(nativeQuery = true, value = """
            SELECT * FROM unique_numbers_seq
            LIMIT :n
            """)
    List<Long> getUniqueNumbers(@Param("n")int n);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE id IN (
                SELECT id FROM hash
                ORDER BY id
                LIMIT :batchSize
            )
            RETURNING *;
            """)
    List<String> getHashBatch(@Param("batchSize") int batchSize);
}
