package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Transactional
    @Modifying
    @Query(value = """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY random()
                    LIMIT :number
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
            """, nativeQuery = true)
    List<String> findAllAndDeleteByPackSize(int number);

    @Query(value = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(916132832, :number)
            """, nativeQuery = true)
    List<Long> getUniqueNumbers(long number);

    @Query(value = "SELECT COUNT(h.hash) FROM hash h", nativeQuery = true)
    Long getHashesSize();
}