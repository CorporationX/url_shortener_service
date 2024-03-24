package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.repository.mapper.UniqueSequenceIdRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Repository
@RequiredArgsConstructor
public class UniqueSequenceIdRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Long> rangeRowMapper = new UniqueSequenceIdRowMapper();

    public List<Long> getNextRange(final int maxRange) {
        String sql = String.format(
                """
                SELECT nextval('unique_hash_number_sequence')
                AS generated_value FROM generate_series(1, %s)
                """, maxRange);
        return jdbcTemplate.query(sql, rangeRowMapper);
    }
}
