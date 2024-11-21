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
    private final static String GET_UNIQUE_NUMBERS_SQL
            = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?);";

    private final static String INSERT_HASH_BATCH = "INSERT INTO public.hash (hash) VALUES (?);";

    private final static String GET_HASH_BATCH = """
                DELETE FROM public.hash
                WHERE hash IN (SELECT hash FROM public.hash ORDER BY random() LIMIT ?)
                RETURNING hash;
        """;

    private final static String GET_HASH_COUNT = "SELECT COUNT(*) FROM public.hash";

    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.repository.batch.size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(GET_UNIQUE_NUMBERS_SQL, Long.class, n);
    }

    @Override
    public void saveBatch(List<String> hashes) {
        jdbcTemplate.batchUpdate(INSERT_HASH_BATCH, new BatchPreparedStatementSetter() {

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
        return jdbcTemplate.queryForList(GET_HASH_BATCH, String.class, batchSize);
    }

    @Override
    public Long getHashCount() {
        return jdbcTemplate.queryForObject(GET_HASH_COUNT, Long.class);
    }
}
