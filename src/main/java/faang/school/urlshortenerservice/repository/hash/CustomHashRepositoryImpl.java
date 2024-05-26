package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.CustomHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CustomHashRepositoryImpl implements CustomHashRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${repository.hash.batchSizeSave}")
    private int batchSizeSave;

    @Override
    public List<Long> getUniqueNumbers(long maxRange) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, maxRange);
    }

    @Override
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING";
        jdbcTemplate.batchUpdate(sql, hashes, batchSizeSave, (ps, hash) -> ps.setString(1, hash));
    }

    @Override
    public List<Hash> getHashBatch(long amount) {
        String sql = """
                DELETE FROM hash
                WHERE hash IN (                     
                   SELECT *
                   FROM hash
                   ORDER BY RANDOM()
                   LIMIT ?)
                returning *;
                """;
        return jdbcTemplate.queryForList(sql, Hash.class, amount);
    }
}
