package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UniqueIdRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${sequence.name}")
    private String sequenceName;

    public Long getNumber() {
        return jdbcTemplate.queryForObject("select nextval(?)", Long.class, sequenceName);
    }
}