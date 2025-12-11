package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    @Query("SELECT u FROM UrlEntity u WHERE u.hash = :hash")
    Optional<UrlEntity> findByHash(@Param("hash") String hash);

    @Query(value = "SELECT * FROM urls u WHERE u.created_at < NOW() - INTERVAL '1 year'", nativeQuery = true)
    List<UrlEntity> findExpired();
}