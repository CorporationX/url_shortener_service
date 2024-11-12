package faang.school.urlshortenerservice.repository.hash.impl;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.hash.FreeHashRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FreeHashRepositoryImpl implements FreeHashRepository {

    private final HashConfig hashConfig;

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES (?)";

        for (int i = 0; i < hashes.size(); i += hashConfig.getInsertBatch()) {
            int end = Math.min(i + hashConfig.getInsertBatch(), hashes.size());

            List<String> batch = hashes.subList(i, end);

            try {
                jdbcTemplate.batchUpdate(sql, batch, batch.size(),
                        (PreparedStatement ps, String hash) -> {
                            ps.setString(1, hash);
                        });
                log.debug("Batch {} processed, containing {} hashes", i / hashConfig.getInsertBatch() + 1, batch.size());
            } catch (DataAccessException dae) {
                log.error("While insert new hashes some error occurred");
                throw new RuntimeException("Error " + dae.getMessage() + " ", dae);
            }
        }
    }

    @Override
    public List<String> findAndDeleteFreeHashes(int amount) {
        String sql = "DELETE FROM hash " +
                "WHERE hash IN (SELECT hash FROM hash LIMIT :range) " +
                "RETURNING hash";

        @SuppressWarnings("unchecked")
        List<String> result = entityManager
                .createNativeQuery(sql)
                .setParameter("range", amount)
                .getResultList();

        return result;
    }
}
