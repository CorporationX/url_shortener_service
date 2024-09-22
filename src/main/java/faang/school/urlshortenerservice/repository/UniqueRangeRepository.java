package faang.school.urlshortenerservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface UniqueRangeRepository {
    List<Long> getNextUniqueRange(int size);
}