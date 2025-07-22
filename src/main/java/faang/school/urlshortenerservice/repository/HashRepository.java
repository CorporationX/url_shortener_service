package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbc;

    public long count() {
        Long count = jdbc.queryForObject("""
                SELECT COUNT(*) FROM hash
                """, Long.class);
        return count != null ? count : 0L;
    }

    public List<Long> getUniqueNumbers(int count) {
        return jdbc.queryForList("""
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, ?)
                """, Long.class, count);
    }

    public void saveBatch(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) return;

        jdbc.update(con -> {
            String sql = """
                    INSERT INTO hash(hash) \
                    SELECT unnest(?::varchar[])
                    """;
            PreparedStatement ps = con.prepareStatement(sql);
            Array array = con.createArrayOf("varchar",
                    hashes.toArray(new String[0]));
            ps.setArray(1, array);
            return ps;
        });
    }

    public List<String> getHashBatch(int size) {
        return jdbc.queryForList("""
                WITH sel AS (
                    SELECT hash FROM hash
                    ORDER BY RANDOM() LIMIT ?
                )
                DELETE FROM hash
                WHERE hash IN (SELECT hash FROM sel)
                RETURNING hash
                """, String.class, size);
    }
}
