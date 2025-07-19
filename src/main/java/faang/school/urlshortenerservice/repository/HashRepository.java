package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :count)
            """)
    List<Long> getNextSequenceValues(@Param("count") long count);

    @Query(nativeQuery = true, value = "SELECT pg_try_advisory_lock(:lockId)")
    boolean tryLock(@Param("lockId") int lockId);

    @Query(nativeQuery = true, value = "SELECT pg_advisory_unlock(:lockId)")
    void unlock(@Param("lockId") int lockId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash
                           FROM hash
                           LIMIT :count)
            RETURNING *
            """)
    List<Hash> findAndDeleteLimit(@Param("count") long count);
}
