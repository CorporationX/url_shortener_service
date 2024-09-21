package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {

    @Value("${number-of-random-hashes}")
    int numberOfRandomHashes;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(long uniqueNumberSeq) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, uniqueNumberSeq);
    }

    @Transactional
    public void save(List<Hash> hashes) {
        if (hashes.isEmpty()) {
            log.info("No hashes to save");
            throw new IllegalArgumentException("Hashes cannot be empty");
        }
        try {
            jdbcTemplate.batchUpdate("INSERT INTO hash (hash) " +
                            " VALUES (?)",
                    hashes,
                    numberOfRandomHashes,
                    (PreparedStatement ps, Hash hash) -> {
                        ps.setString(1, hash.getHash());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public List<Hash> getHashBatch(long amount) {
        String sql = "SELECT hash FROM hash LIMIT ?";
        List<Hash> hashesOrders = jdbcTemplate.queryForList(sql, Hash.class, amount);

        if (!hashesOrders.isEmpty()) {
            List<String> stringHashes = hashesOrders.stream()
                    .map(String::valueOf)
                    .toList();
            String deleteSql = "DELETE FROM hash WHERE hash = ?";
            jdbcTemplate.batchUpdate(deleteSql, stringHashes, stringHashes.size(), ((ps, argument) -> ps.setString(1, argument)));
        }
        return hashesOrders;
    }
}
