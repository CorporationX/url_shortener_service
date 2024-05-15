package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Hash, Long> {

    Optional<Url> getByHash(String hash);

    @Query() //TODO: запрос написать или так сделать, надо подумать
    public Optional<Url> findByHash(String hash) {
        // SQL запрос для поиска URL по хэшу
        String sql = "SELECT * FROM url WHERE hash = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Url url = new Url();
            url.setUrl(rs.getString("url"));
            url.setHash(rs.getString("hash"));
            return url;
        }, hash);
    }
}
