package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch-size}")
    private Integer batchSize;
    private final JdbcTemplate jdbcTemplate;
    private final HashJpaRepository hashJpaRepository;

    public void batchSave(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (HASH) VALUES (?)",
                hashes,
                batchSize,
                (PreparedStatement preparedStatement, String hash) -> preparedStatement.setString(1, hash)
        );

    }
}
