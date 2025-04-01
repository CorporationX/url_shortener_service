package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:1000}")
    private int batchSize;

    @Transactional
    public List<String> saveAll(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?) ON CONFLICT DO NOTHING",
                hashes,
                batchSize,
                (ps, hash) -> ps.setString(1, hash)
        );

        String inClause = String.join(",",
                Collections.nCopies(hashes.size(), "?"));

        return jdbcTemplate.queryForList(
                "SELECT hash FROM hash WHERE hash IN (" + inClause + ")",
                String.class,
                hashes.toArray()
        );
    }

    @Transactional
    public List<String> getHashBatch(int numbers) {
        String sql = """
                DELETE FROM hash
                WHERE hash
                IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT ? FOR UPDATE SKIP LOCKED)
                RETURNING hash;
                """;
        return jdbcTemplate.query(sql, ps -> ps.setInt(1, numbers),
                (rs, rowNum) -> rs.getString("hash"));
    }

    @Transactional
    public List<Long> getUniqueNumbers(int numbers) {
        String sql = """
                SELECT nextval('unique_hash_number_seq') AS unique_number FROM generate_series(1, ?)
                """;
        return jdbcTemplate.query(sql, ps -> ps.setInt(1, numbers),
                (rs, rowNum) -> rs.getLong("unique_number"));
    }

    @Transactional
    public long count() {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT count(*) FROM hash", Long.class))
                .orElse(0L);
    }

    @Transactional
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM hash");
    }
}
