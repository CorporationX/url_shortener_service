package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value =
            """
                DELETE FROM hashes
                USING (
                    SELECT id
                    FROM hashes
                    ORDER BY id ASC
                    LIMIT :amount
                ) AS sub_query
                WHERE hashes.id = sub_query.id
                RETURNING hashes.*;
            """)
    List<Hash> findAndDelete(long amount);
}
