package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HashCustomRepositoryImpl implements HashCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllHashesBatched(List<Hash> hashes) {
        String sql = "INSERT INTO hashes (hash) VALUES (?)";

        try {
            jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                    (ps, hash) -> ps.setString(1, hash.getHash()));
        } catch (DataAccessException e) {
            log.error("Error occurred while batch save!", e);
            throw new RuntimeException("Error! " + e.getMessage() + " ", e);
        }
    }
}
