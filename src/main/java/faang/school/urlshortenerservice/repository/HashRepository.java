package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, HashCustomRepository {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :amount)
            """)
    List<Long> getUniqueNumbers(int amount);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hashes WHERE hash IN (SELECT hash FROM hashes LIMIT ?) RETURNING *
            """)
    List<Hash> getHashBatch(int batchSize);
}
