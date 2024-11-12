package faang.school.urlshortenerservice.repository.postgres.hash;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class HashRepositoryImpl implements IHashRepository {
    private static final String SAVE_BATCH_QUERY = "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveBatch(List<Hash> hashes) {
        jdbcTemplate.batchUpdate(
                SAVE_BATCH_QUERY,
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash.getHash())
        );
    }
}
