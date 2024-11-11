package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.repository.batch.batch-size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?);";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Override
    public void save(List<String> hashes) {
        String sql = "INSERT INTO public.hash (hash) VALUES (?);";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Override
    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM public.hash
                WHERE hash IN (SELECT hash FROM public.hash ORDER BY random() LIMIT ?)
                RETURNING hash;
        """;
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
