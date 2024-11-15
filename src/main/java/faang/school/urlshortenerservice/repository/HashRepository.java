package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
              SELECT nextval ('unique_hash_number_seq')
              FROM generate_series(1,:n)
            """)
    List<Long> getUniqueNumbers(int n);

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH cte AS (
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount FOR UPDATE SKIP LOCKED
            )
            DELETE FROM hash
            USING cte
            WHERE hash.id = cte.id
            RETURNING hash.*
            """)
    List<Hash> getHashesBatch(@Param("amount") int amount);
}
