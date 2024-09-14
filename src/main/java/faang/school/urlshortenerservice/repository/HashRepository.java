package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final HashRepositoryJpa hashRepositoryJpa;

    private final String INSERT_QUERY= "INSERT INTO hash (hash) VALUES (?)";

    @Value("${spring.jpa.properties.hibernate.jdbc.batch-size}")
    private int batchSize;

    public long getSize() {
        return hashRepositoryJpa.count();
    }

    public List<Long> getUniqueNumbers(int count) {
        return hashRepositoryJpa.getUniqueNumbers(count);
    }

    public List<Hash> getHashes(int count) {
        return hashRepositoryJpa.getHashBatch(count);
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(INSERT_QUERY, hashes, batchSize, (ps, argument) -> {
            ps.setString(1, argument);
        });
    }
}
