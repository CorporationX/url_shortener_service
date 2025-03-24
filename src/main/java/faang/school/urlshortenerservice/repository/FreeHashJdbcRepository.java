package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.FreeHash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FreeHashJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteByIds(List<String> hashes) {
        if (hashes.isEmpty()) {
            return;
        }

        String sql = "DELETE FROM free_hashes WHERE hash = ANY(?)";
        jdbcTemplate.update(sql, ps -> ps.setArray(1, ps.getConnection().createArrayOf("text", hashes.toArray())));
    }

    public FreeHash deleteOneFreeHashAndReturnHash() {
        String sql = """
        DELETE FROM free_hashes
        WHERE hash = (
            SELECT hash
            FROM free_hashes
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        )
        RETURNING hash
    """;
        String object = jdbcTemplate.queryForObject(sql, String.class);
        return new FreeHash(object);
    }
}
