package faang.school.urlshortenerservice.repositories;

import faang.school.urlshortenerservice.entities.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query("""
    select u.url
        from Url u
        where u.hash = :hash
    """)
    String findUrlByHash(String hash);
}
