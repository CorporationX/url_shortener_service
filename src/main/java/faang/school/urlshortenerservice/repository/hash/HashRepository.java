package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, CustomHashRepository {

    @Query(value = "SELECT NEXTVAL('unique_number_seq') FROM GENERATE_SERIES(1, :batchSize)", nativeQuery = true)
    List<Long> getUniqueNumbers(long batchSize);

    @Modifying
    @Query(value = """
            DELETE FROM hashes
            WHERE hash IN
            (
                SELECT hash FROM hashes LIMIT :amount
            )
            RETURNING *
            """, nativeQuery = true)
    List<Hash> findAndDelete(int amount);
}
