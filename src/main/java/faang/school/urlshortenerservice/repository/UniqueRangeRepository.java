package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UniqueRangeRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getNextUniqueRange(int size) {
        String q = "SELECT nextval('unique_hash_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(q, Long.class, size);
    }
}
