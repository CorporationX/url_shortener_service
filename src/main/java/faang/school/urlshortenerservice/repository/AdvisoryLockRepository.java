package faang.school.urlshortenerservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdvisoryLockRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdvisoryLockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean acquireLock(long lockId) {
        return jdbcTemplate.queryForObject(
                "SELECT pg_try_advisory_xact_lock(?)",
                Boolean.class,
                lockId
        );
    }
}
