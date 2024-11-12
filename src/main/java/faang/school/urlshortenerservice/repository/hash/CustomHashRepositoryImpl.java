package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CustomHashRepositoryImpl implements CustomHashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveAllCustom(List<Hash> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        try {
            log.debug("Inserting {} amount of hash into db", hashes.size());
            jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                    (PreparedStatement ps, Hash hash) -> ps.setString(1, hash.getHash()));
        } catch (DataAccessException dae) {
            log.error("Error occurred while batch save!", dae);
            throw new RuntimeException("Error! " + dae.getMessage() + " ", dae);
        }
    }
}
