package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean tryAcquireLock(String lockName) {
        String normalizedLockName = lockName.toLowerCase().trim();

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT pg_try_advisory_xact_lock(hashtext(?)::bigint)",
                Boolean.class,
                normalizedLockName
        ));
    }
}
