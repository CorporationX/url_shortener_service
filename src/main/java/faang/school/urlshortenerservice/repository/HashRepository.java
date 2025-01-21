package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

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
