package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomHashRepositoryImpl implements CustomHashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void saveAllBatched(List<Hash> hashes) {
        log.info("Thread {} start save - {} element", Thread.currentThread().getName(), hashes.size());
        try {
            String sql = "INSERT INTO hashes (hash) VALUES (?)";
            jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                    (ps, hash) -> ps.setString(1, hash.getHash()));
        } catch (DataAccessException dae) {
            log.error("Error occurred while batch save!", dae);
            throw new RuntimeException("Error! " + dae.getMessage() + " ", dae);
        }
        log.info("Thread {} finish save", Thread.currentThread().getName());
    }
}