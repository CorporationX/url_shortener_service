package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final RepositoryUrlService repositoryUrlService;
    private final JdbcTemplate jdbcTemplate;
    private final HashRepository hashRepository;

    @Transactional
    public void reuseOldHashes(long batchSize, long offset) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        String selectSql = "SELECT hash FROM url WHERE created_at < ? LIMIT ?";
        List<Map<String, Object>> hashesOrders = jdbcTemplate.queryForList(selectSql, oneYearAgo, batchSize, offset);

        if (!hashesOrders.isEmpty()) {
            List<Hash> hashes = hashesOrders.stream()
                    .map(hash -> (Hash) hash.get("hash"))
                    .toList();
            hashRepository.save(hashes);

            List<String> stringHashes = hashes.stream()
                    .map(String::valueOf)
                    .toList();
            String deleteSql = "DELETE FROM url WHERE hash = ?";
            jdbcTemplate.batchUpdate(deleteSql, stringHashes, stringHashes.size(),
                    ((ps, argument) -> ps.setString(1, argument)));
        }
    }

    @Transactional
    public void createUrl(Url url) {
        repositoryUrlService.save(url);
    }

    @Transactional
    public String getUrlByHash(String hash) {
        String selectSql = "SELECT hash FROM url WHERE hash = ?";
        return jdbcTemplate.queryForList(selectSql, hash).toString();
    }

}
