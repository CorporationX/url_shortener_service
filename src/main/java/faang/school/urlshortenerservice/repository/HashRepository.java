package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            WITH cte AS (
              SELECT hash
                FROM hash
                FOR UPDATE SKIP LOCKED
                LIMIT :n
            )
            DELETE FROM hash
            USING cte
            WHERE hash.hash = cte.hash
            RETURNING cte.hash;
            """)
    List<String> getHashBatch(@Param("n") int n);
}
