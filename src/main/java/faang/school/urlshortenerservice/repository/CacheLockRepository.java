package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class CacheLockRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryLock() {
        String sql = """
            UPDATE cache_lock
            SET locked = true,
                last_updated = CURRENT_TIMESTAMP
            WHERE id = 1
            AND (locked = false OR last_updated < NOW() - INTERVAL '5 seconds')
        """;

        return jdbcTemplate.update(sql) == 1;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unlock() {
        String sql = """
                UPDATE cache_lock
                SET locked = false
                WHERE id = 1;
                """;
        jdbcTemplate.update(sql);
    }
}
