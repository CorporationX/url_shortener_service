package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<String> findByHash(String hash) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select url.url from url where hash=?", String.class, hash));
        } catch (EmptyResultDataAccessException e){
            throw new EmptyResultDataAccessException("url not found", 1);
        }
    }

    public int saveAssociation(String url, String hash) {
        return jdbcTemplate.update("insert into url(url, hash) values(?, ?)", url, hash);
    }

}
