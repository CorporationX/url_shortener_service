package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repositories.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${url-shortener.hash.range}")
    private int hashRange;
    
    @Value("${url-shortener.hash.batch-size}")
    private int batchSize;
    
    @Value("${url-shortener.hash.base62-characters}")
    private String base62Characters;

    @Scheduled(cron = "${url-shortener.hash.generation.cron}")
    @Transactional
    public void generateHash() {
        List<String> hashes = hashRepository.getNextRange(hashRange).stream()
                .map(this::applyBase62Encoding)
                .toList();
        saveHashes(hashes, batchSize);
    }

    @Transactional
    public List<String> getHash(long amount) {
        List<String> hashValues = hashRepository.findAndDelete(amount);

        if (hashValues.size() < amount) {
            generateHash();
            hashValues.addAll(hashRepository.findAndDelete(amount - hashValues.size()));
        }
        
        return hashValues;
    }

    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashAsync(long amount) {
        return CompletableFuture.completedFuture(getHash(amount));
    }

    public String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(base62Characters.charAt((int) (number % base62Characters.length())));
            number /= base62Characters.length();
        }
        return builder.toString();
    }

    @Transactional
    public void saveHashes(List<String> hashes, int batchSize) {
        String sql = "insert into hash (hash) values (?)";

        for (int i = 0; i < hashes.size(); i += batchSize) {
           int end = Math.min(i + batchSize, hashes.size());
           List<String> sublist = hashes.subList(i, end);

           jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
               @Override
               public void setValues(PreparedStatement ps, int index) throws SQLException {
                   ps.setString(1, sublist.get(index));
               }

               @Override
               public int getBatchSize() {
                   return sublist.size();
               }
           });
        }
    }
}
