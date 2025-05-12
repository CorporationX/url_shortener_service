package faang.school.urlshortenerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDatabaseCleaner {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void restartUniqueNumberSequence() {
        String sql = "ALTER SEQUENCE unique_number_seq RESTART WITH 1";
        jdbcTemplate.execute(sql);
    }

    @Transactional
    public void truncateHashTable() {
        String sql = "TRUNCATE TABLE hash";
        jdbcTemplate.execute(sql);
    }

    @Transactional
    public void truncateUrlTable() {
        String sql = "TRUNCATE TABLE url";
        jdbcTemplate.execute(sql);
    }

    public void flushRedisDatabase() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }
}
