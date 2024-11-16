package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
        select
            *
        from url
        where hash = ?1
        """)
    Optional<Url> findByShortUrl(String shortUrl);

    @Query(nativeQuery = true, value = """
        delete from url
        where created_at < ?1
        returning *
        """)
    @Modifying
    @Transactional
    List<Url> deleteExpiredUrl(LocalDateTime localDateTime);
}
