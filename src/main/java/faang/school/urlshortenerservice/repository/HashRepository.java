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
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange);
            """)
    List<Long> getNextRange(@Param("maxRange") int maxRange);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
                WITH deleted AS (
                    SELECT * FROM hash
                    LIMIT :amount
                    FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hash
                USING deleted
                WHERE hash.hash = deleted.hash
                RETURNING hash.*
            """)
    List<Hash> findAndDelete(@Param("amount") long amount);
}
