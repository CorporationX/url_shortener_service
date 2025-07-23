package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
        select * from urls u where u.hash = :hash
        """)
    Optional<Url> findByHash(@Param("hash") String hash);

    @Query(nativeQuery = true, value = """
        select * from urls u where u.url = :url
        """)
    Optional<Url> findByUrl(@Param("url") String url);

    @Modifying
    @Query(nativeQuery = true, value = """
        delete from urls u
         where u.created_at + make_interval(months => :interval) < now()
         returning *
        """)
    List<Url> clearOldUrls(@Param("interval") int interval);
}
