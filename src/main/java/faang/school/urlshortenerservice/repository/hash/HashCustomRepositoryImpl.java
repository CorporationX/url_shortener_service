package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HashCustomRepositoryImpl implements HashCustomRepository {

    private static final String SQL = "INSERT INTO hashes (hash) VALUES (?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllHashesBatched(List<Hash> hashes) {
        try {
            jdbcTemplate.batchUpdate(SQL, hashes, hashes.size(),
                    (ps, hash) -> ps.setString(1, hash.getHash()));
        } catch (DataAccessException e) {
            log.error("Error occurred while batch save!", e);
            throw new RuntimeException("Error! " + e.getMessage() + " ", e);
        }
    }
}
