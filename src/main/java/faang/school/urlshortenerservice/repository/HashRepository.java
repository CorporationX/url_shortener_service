package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

   // @Value("${batch.sizze}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        String query = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(query, Long.class, n);
    }

    public void save(List<String> hashes){
        String query = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(query, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        String query = "WITH deleted AS ( " +
                "DELETE FROM hash " +
                "WHERE hash IN ( " +
                "SELECT hash FROM hash " +
                "ORDER BY random() LIMIT ?) " +
                "RETURNING hash) " +
                "SELECT hash FROM deleted";
        return jdbcTemplate.queryForList(query, String.class, batchSize);
    }
}
