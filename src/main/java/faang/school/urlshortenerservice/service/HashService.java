package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final JdbcTemplate jdbcTemplate;
    private final HashRepository hashRepository;

    @Value("${url-shortener.batch-size}")
    private int batchSize;

    public void saveHashByBatch(List<Hash> hashes) {

        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", hashes,
                batchSize,
                (ps, argument) -> ps.setString(1, argument.getHash()));
    }

    public List<Long> getUniqueNumbers(long countNumbers) {
        return hashRepository.getUniqueNumbers(countNumbers);
    }
}
