package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Long,Url> {
    @Query("SELECT h.url FROM Hash h WHERE h.hash = :hash")
    Url getUrlByHash(@Param("hash") String hash);
}
