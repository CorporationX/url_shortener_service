package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("select u.url from Url u where u.hash = :hash")
    Optional<String> findUrlForCache(@Param("hash") String hash);

    @Modifying
    @Query(value = """
            delete from url
            where created_at < : date
            returning hash, created_at"""
    , nativeQuery = true)
    List<Hash> deleteOldUrls(LocalDateTime date);
}
