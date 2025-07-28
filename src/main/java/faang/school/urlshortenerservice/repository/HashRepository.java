package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
                FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(@Param("count") int count);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
                WHERE hash in (
                    SELECT hash FROM hash
                        FOR UPDATE SKIP LOCKED
                    LIMIT :count
                )
            RETURNING hash
            """)
    List<String> getAndDeleteHashBatch(@Param("count") int count);

    @Query(nativeQuery = true, value = """
            SELECT pg_try_advisory_lock(:key)
            """)
    boolean tryAdvisoryLock(@Param("key") long key);

    @Query(nativeQuery = true, value = """
            SELECT pg_advisory_unlock(:key)
            """)
    boolean unlockAdvisoryLock(@Param("key") long key);



}
