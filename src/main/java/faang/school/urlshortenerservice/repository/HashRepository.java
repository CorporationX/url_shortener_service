package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(int n);

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH to_delete AS (
                SELECT hash
                FROM hash
                FOR UPDATE SKIP LOCKED
                LIMIT :count
            )
            DELETE FROM hash
            USING to_delete
            WHERE hash.hash = to_delete.hash
            RETURNING hash.hash;
            """)
    List<String> getFreeHashesLocked(long count);
}
