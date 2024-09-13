package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    public int saveAssociation(String url, String hash) {
        log.info("Url {} and hash {} was saved successfully in database", url, hash);
        return jdbcTemplate.update("insert into url(url, hash) values(?, ?)", url, hash);
    }

}
