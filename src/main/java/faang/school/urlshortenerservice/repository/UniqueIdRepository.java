package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UniqueIdRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getNextIds(int limit) {
        return jdbcTemplate.queryForList(
                """
                        SELECT nextval('unique_number_seq')
                        FROM generate_series(1, ?)
                        """,
                Long.class,
                limit
        );
    }
}