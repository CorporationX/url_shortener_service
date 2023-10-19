package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.cache.limit}")
    private int limit;

    public void saveHashes(List<String> hashes) {

        String sql = "INSERT INTO hash (hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public CompletableFuture<List<String>> findAndDelete() {
        return CompletableFuture.supplyAsync(() -> hashRepository.findAndDelete(limit))
                .exceptionally((e) -> {
                    e.printStackTrace();
                    return Collections.emptyList();
                });
    }
}
