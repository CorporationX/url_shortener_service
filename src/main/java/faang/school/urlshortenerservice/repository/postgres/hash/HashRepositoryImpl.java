package faang.school.urlshortenerservice.repository.postgres.hash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HashRepositoryImpl implements IHashRepository {
    private static final String SAVE_BATCH_QUERY = "INSERT INTO hash (hash) VALUES (?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveBatch(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                SAVE_BATCH_QUERY,
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }
}
