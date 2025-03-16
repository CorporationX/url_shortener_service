package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashCustomRepositoryImpl implements HashCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void hashBatchSave(List<Hash> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash.getHash()));
    }
}
