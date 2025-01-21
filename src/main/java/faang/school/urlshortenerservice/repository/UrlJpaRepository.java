package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlJpaRepository extends JpaRepository<Url, String> {
    Url findByHash(String hash);
    @Query("SELECT u FROM Url u WHERE u.hash = :hash")
    Url getUrlByHash(@Param("hash") String hash);
}
