package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashJpaRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
                SELECT nextval ('unique_number_seq')
                FROM generate_series(1, :count)
                """)
    List<Long> getNumbersFromSequence(Integer count);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> getAndDeleteHashes(int amount);
}
