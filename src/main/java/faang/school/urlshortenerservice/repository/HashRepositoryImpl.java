package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int count) {
        return jdbcTemplate.queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)", Long.class, count);
    }

    @Override
    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash VALUES (?)",
                hashes,
                batchSize,
                (ps, hash) -> ps.setString(1, hash));
    }

    @Override
    public List<String> getHashBatch() {
        return jdbcTemplate.queryForList("DELETE FROM hash\n" +
                "WHERE ctid IN (\n" +
                "    SELECT ctid\n" +
                "    FROM hash\n" +
                "    ORDER BY random()\n" +
                "    LIMIT ?\n" +
                ") returning hash", String.class, batchSize);
    }
}
