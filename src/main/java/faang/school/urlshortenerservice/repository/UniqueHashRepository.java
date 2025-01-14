package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueHashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1,:maxCountRange)
            """)
    List<Long> getNextRange(int maxCountRange);


    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (
                SELECT id FROM hash ORDER BY id ASC LIMIT :amount
            ) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);

}
