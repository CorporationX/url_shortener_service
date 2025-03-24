package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Long> getUniqueNumbers(int fetchSize) {
        String query = "SELECT nextval('unique_number_seq') AS next_value FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(query, Long.class, fetchSize);
    }

    @Override
    @Transactional
    public void saveAll(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)",
                hashes,
                batchSize,
                (PreparedStatement ps, String hash) -> ps.setString(1, hash));
    }

    @Override
    @Transactional
    public List<String> getHashBatch(int fetchSize) {
        String query = "DELETE FROM hash " +
                "  WHERE hash IN (" +
                "    SELECT hash " +
                "    FROM hash " +
                "    LIMIT " + fetchSize +
                "  ) " +
                "  RETURNING hash";
        RowMapper<String> rowMapper = (r, i) -> r.getString("hash");
        return jdbcTemplate.query(query, rowMapper);
    }
}
