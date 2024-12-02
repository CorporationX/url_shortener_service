package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch-size}")
    private Integer batchSize;
    @Value("${hash.count-of-numbers-from-the-sequence}")
    private Integer countOfNumbersFromTheSequence;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        String query = "SELECT nextval('unique_number_seq') from generate_series(1,:n)";
        Map<String, Integer> namedParams = Collections.singletonMap("n", n);
        return namedParameterJdbcTemplate.queryForList(query, namedParams, Long.class);
    }

    @Transactional
    public void save(List<String> hashes) {
        String query = "INSERT INTO hash(hash) VALUES (:value)";

        List<Map<String, String>[]> batches = new ArrayList<>();

        int i;
        for (i = 0; i < hashes.size() - batchSize; i += batchSize) {
            batches.add(getMapParams(hashes, i, i + batchSize));
        }
        batches.add(getMapParams(hashes, i, hashes.size()));

        for (Map<String, String>[] param : batches) {
            namedParameterJdbcTemplate.batchUpdate(query, param);
        }

        log.info("batches save to database, count: {}", batches.size());
    }

    @Transactional
    public List<String> getHashBatch() {
        String query = "delete from hash where hash IN (select hash from hash order by random() limit :limit) returning *;";
        return namedParameterJdbcTemplate.queryForList(query,
                Collections.singletonMap("limit", countOfNumbersFromTheSequence), String.class);
    }

    private Map<String, String>[] getMapParams(List<String> hashes, int start, int end) {
        return hashes.subList(start, end).stream()
                .map(val -> Collections.singletonMap("value", val))
                .toList().toArray(new Map[0]);
    }
}
