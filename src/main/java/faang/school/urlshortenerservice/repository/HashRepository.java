package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${url-shortener.hash.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        List<Long> result = new ArrayList<>(n);
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?);";

        jdbcTemplate.query(sql, ps -> ps.setInt(1, n), rs -> {
            while (rs.next()) {
                result.add(rs.getLong(1));
            }
        });

        return result;
    }

    public void save(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING";

        jdbcTemplate.batchUpdate(sql,
                hashes,
                hashes.size(),
                (PreparedStatement ps, String hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT ?) RETURNING hash";
        List<String> result = new ArrayList<>();

        jdbcTemplate.query(sql, ps -> ps.setInt(1, batchSize), rs -> {
            while (rs.next()) {
                result.add(rs.getString("hash"));
            }
        });

        return result;
    }
}