package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
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
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :range)
            """)
    List<Long> getNextRange(@Param("range") int range);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        WITH cte AS (
            SELECT hash FROM hash ORDER BY hash ASC LIMIT :amount FOR UPDATE SKIP LOCKED
        )
        DELETE FROM hash
        USING cte
        WHERE hash.hash = cte.hash
        RETURNING hash.*
        """)
    List<Hash> findAndDelete(@Param("amount") long amount);
}
