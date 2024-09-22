package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public String findByHash(String hash) {
        try {
            Optional<String> url = Optional.ofNullable(jdbcTemplate.queryForObject("select url.url from url where hash=?", String.class, hash));
            if (url.isPresent()) {
                log.info("Found url: {} in data base", url.get());
                return url.get();
            } else {
                log.error("Hash {} was not found", hash);
                throw new NotFoundException("hash was not found");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("url not found", 1);
        }
    }

    public Optional<String> findByUrl(String url) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select url.hash from url where url=?", String.class, url));
        } catch (EmptyResultDataAccessException e) {
            log.info("Url {} was not found", url);
            return Optional.empty();
        }
    }

    public int saveAssociation(String url, String hash) {
        log.info("Url {} and hash {} was saved successfully in database", url, hash);
        return jdbcTemplate.update("insert into url(url, hash) values(?, ?)", url, hash);
    }

    public List<String> getUnusedHashesForPeriod(Period range) {
        LocalDateTime period = LocalDateTime.now().minus(range);
        List<String> unusedHashes = new ArrayList<>();
        unusedHashes = jdbcTemplate.query(
                "delete from url where url.created_at in " +
                        "(select url.created_at from url where url.created_at<=?) " +
                        "returning url.hash"
                , (rs, rowNum) -> rs.getString("hash")
                , period);
        log.info("managed to get {} unused hashes from {}", unusedHashes.size(), period);
        return unusedHashes;
    }
}
