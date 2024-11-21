package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
        DELETE FROM public.url u
        WHERE u.created_at < now() - interval '365 days'
        RETURNING u.hash;
        """)
    List<String> getExpiredHashes();

    Optional<Url> findByUrl(String url);
}
