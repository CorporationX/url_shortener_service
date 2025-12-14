package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final HashJpaRepository hashJpaRepository;

    @Value("${hash.batch.size:1000}")
    private int batchSize;

    /**
     * Получает n уникальных чисел из sequence unique_number_seq
     *
     * @param n количество уникальных чисел
     * @return список уникальных чисел
     */
    public List<Long> getUniqueNumbers(int n) {
        log.debug("Getting {} unique numbers from sequence", n);
        return hashJpaRepository.getUniqueNumbers(n);
    }

    /**
     * Сохраняет список хэшей батчом в таблицу hash
     *
     * @param hashes список хэшей для сохранения
     */
    @Transactional
    public void save(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            log.warn("Empty hash list provided, skipping save");
            return;
        }

        log.debug("Saving {} hashes in batch", hashes.size());

        String sql = "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, hash) -> {
            ps.setString(1, hash);
        });

        log.debug("Successfully saved {} hashes", hashes.size());
    }

    /**
     * Получает n случайных хэшей из таблицы hash и удаляет их
     *
     * @return список случайных хэшей
     */
    public List<String> getHashBatch() {
        log.debug("Getting batch of {} random hashes", batchSize);

        List<String> hashes = hashJpaRepository.getAndDeleteRandomHashes(batchSize);

        log.debug("Retrieved and deleted {} hashes", hashes.size());
        return hashes;
    }
}