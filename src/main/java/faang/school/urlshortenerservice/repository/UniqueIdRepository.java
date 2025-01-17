package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UniqueIdRepository {

    private static final String GET_NEXT_RANGE = """
            SELECT nextval('unique_hash_number_seq')
            FROM generate_series(1, ?);
            """;

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getNextRange(int amount) {
        return jdbcTemplate.queryForList(GET_NEXT_RANGE, Long.class, amount);
    }
}
