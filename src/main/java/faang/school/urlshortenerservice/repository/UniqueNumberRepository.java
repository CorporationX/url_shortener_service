package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UniqueNumberRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${cache.capacity:10000}")
    private int cacheCapacity;

    public List<Long> getUniqueNumbers() {
        return jdbcTemplate.queryForList(
                String.format("SELECT nextval('unique_number_seq') FROM generate_series(1, %d)", cacheCapacity),
                Long.class
        );
    }

    public void resetSequence() {
        String sql = "ALTER SEQUENCE unique_number_seq RESTART WITH 1";
        jdbcTemplate.execute(sql);
    }
}
