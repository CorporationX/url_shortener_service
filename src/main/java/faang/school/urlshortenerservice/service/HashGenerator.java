package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final JdbcTemplate jdbcTemplate;

    public List<String> generateHashes(int count) {
        return base62Encoder.encodeBatch(getNextSequenceBatch(count));
    }

    private List<Long> getNextSequenceBatch(int count) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                Long.class,
                count
        );
    }
}