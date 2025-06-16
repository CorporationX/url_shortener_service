package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SequenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int quantity) {
        String sql = """
                SELECT nextval('unique_number_seq') AS generated_value
                FROM generate_series(1, ?)
                """;
        return jdbcTemplate.queryForList(sql, Long.class, quantity);
    }
}
