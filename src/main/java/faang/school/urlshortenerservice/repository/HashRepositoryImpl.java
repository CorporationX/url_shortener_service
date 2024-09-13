package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash (hash_value) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, argument) -> ps.setString(1, argument));
    }
}
