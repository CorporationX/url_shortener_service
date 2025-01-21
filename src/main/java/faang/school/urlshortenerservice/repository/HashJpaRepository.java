package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashJpaRepository extends JpaRepository<Hash, String> {

    //    private final JdbcTemplate jdbcTemplate;
//

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :amount)")
    List<Long> getUniqueNumbers(long amount);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash 
            WHERE hash in (SELECT hash FROM hash LIMIT ?) 
            RETURNING HASH
            """)

    public List<String> getHashBatch(int batchSize);
}
