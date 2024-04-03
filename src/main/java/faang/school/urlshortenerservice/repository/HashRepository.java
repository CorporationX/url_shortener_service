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
                DELETE FROM hash
                USING (
                    SELECT id
                    FROM hash
                    ORDER BY id ASC
                    LIMIT :amount
                ) AS sub_query
                WHERE hash.id = sub_query.id
                RETURNING hash.*;
            """)
    List<Hash> findAndDelete(long amount);
}
