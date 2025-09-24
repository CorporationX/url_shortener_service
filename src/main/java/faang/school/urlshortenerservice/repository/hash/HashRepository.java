package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :n)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Query(value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT h.hash
                FROM hash h
                ORDER BY h.hash
                FOR UPDATE SKIP LOCKED
                LIMIT :limit
            )
            RETURNING hash
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("limit") int limit);
}
