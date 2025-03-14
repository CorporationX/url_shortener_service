package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    @Value("${hash_repo.save_batch_size}")
    private int SAVE_BATCH;

    @Value("${hash_repo.hash_batch_size}")
    private int HASH_SIZE;

    private final JdbcTemplate template;

    public List<Long> getUniqueNumbers(int UNIQUE_MAX_SIZE) {
        String sql = "SELECT nextval('unique_number_sequence') FROM generate_series(1, ?);";
        return template.queryForList(sql, Long.class, UNIQUE_MAX_SIZE);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES(?)";
        template.batchUpdate(sql, hashes, SAVE_BATCH,
                (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hash
                WHERE hash IN (SELECT hash FROM hash LIMIT ?)
                RETURNING hash;
                """;
        return template.queryForList(sql, String.class, HASH_SIZE);
    }
}