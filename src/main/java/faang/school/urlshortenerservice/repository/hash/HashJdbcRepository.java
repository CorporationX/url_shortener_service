package faang.school.urlshortenerservice.repository.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Repository
public class HashJdbcRepository {
    private static final String SAVE_HASHES_BY_BATCH_SQL = """
                INSERT INTO hash (hash)
                VALUES (?)
                """;

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.jdbc-template.batch-size.save-hashes}")
    private int batchSize;

    public void saveAllBatch(List<String> hashes) {
        int totalBatches = (int) Math.ceil((double) hashes.size() / batchSize);

        IntStream.range(0, totalBatches).forEach(i -> {
            int fromIndex = i * batchSize;
            int toIndex = Math.min((i + 1) * batchSize, hashes.size());

            List<String> batch = hashes.subList(fromIndex, toIndex);

            jdbcTemplate.batchUpdate(SAVE_HASHES_BY_BATCH_SQL, batch, batchSize, (ps, hash) -> ps.setString(1, hash));
        });
    }
}
