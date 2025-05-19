package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(value = """
                    SELECT nextval('public.unique_number_seq')
                    FROM generate_series(1, :n)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Query(value = """
                    INSERT INTO hash (hash)
                    SELECT unnest(CAST(:hashes AS varchar[]))
                    ON CONFLICT DO NOTHING
                    RETURNING hash
            """, nativeQuery = true)
    List<String> saveAllBatch(@Param("hashes") String[] hashes);

    @Modifying
    @Query(value = """
                    WITH deleted_hashes AS (
                        DELETE FROM hash
                        WHERE ctid IN (SELECT ctid FROM hash LIMIT :limit)
                        RETURNING hash
                    )
                    SELECT * FROM deleted_hashes
            """, nativeQuery = true)
    List<String> getHashBatch(@Param("limit") int limit);
}
