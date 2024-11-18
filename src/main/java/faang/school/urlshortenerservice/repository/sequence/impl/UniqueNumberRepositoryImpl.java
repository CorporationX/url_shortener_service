package faang.school.urlshortenerservice.repository.sequence.impl;

import faang.school.urlshortenerservice.config.sequence.NumberSequenceProperties;
import faang.school.urlshortenerservice.repository.sequence.UniqueNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UniqueNumberRepositoryImpl implements UniqueNumberRepository {

    private final NumberSequenceProperties numberSequenceProperties;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers() {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

        try {
            return jdbcTemplate.queryForList(sql, Long.class, numberSequenceProperties.getGenerationBatch());
        } catch (DataAccessException dae) {
            log.error("While getUniqueNumbers() some error occurred");
            throw new RuntimeException("Error " + dae.getMessage() + " ", dae);
        }
    }
}
