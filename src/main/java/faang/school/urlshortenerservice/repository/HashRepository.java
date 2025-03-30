package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch.size:1000}")
    private int batchSize;


    public List<Long> getUniqueNumbers(int maxRange){
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_numbers_seq') FROM generate_series(1, ?)",
                Long.class,
                maxRange
        );
    }

    @Transactional
    public void save(List<String> hashes){
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash_value) VALUES (?)",
                hashes,
                batchSize,
                (PreparedStatement ps, String hash) -> ps.setString(1, hash)
        );
    }

    public List<Hash> getHashBatch(long amount){
        return jdbcTemplate.queryForList(
                "DELETE FROM hash WHERE ctid IN (SELECT ctid FROM hash ORDER BY RANDOM() LIMIT :amount) RETURNING hash_value",
                Hash.class,
                batchSize
        );
    }
}
