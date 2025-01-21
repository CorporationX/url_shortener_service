package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashJpaRepository extends CrudRepository<Hash, String> {
    @Query(nativeQuery = true, value = "SELECT NEXTVAL('unique_hash_number_seq') FROM generate_series(1, :maxRange)")
    List<Long> getNextRange(int maxRange);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (
            SELECT hash FROM hash ORDER BY hash ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}