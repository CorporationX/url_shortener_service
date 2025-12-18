package faang.school.urlshortenerservice.repository.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(int maxRange) {
        return jdbcTemplate.queryForList("""
                SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
                """, Long.class, maxRange);
    }

    @Override
    public List<String> getHashBatch(long amount) {
        return jdbcTemplate.queryForList("""
                        DELETE FROM hash WHERE hash IN (SELECT hash FROM hash limit ?) RETURNING *
                """, String.class, amount);
    }

    @Override
    public void saveHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash(hash) VALUES(?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }
}