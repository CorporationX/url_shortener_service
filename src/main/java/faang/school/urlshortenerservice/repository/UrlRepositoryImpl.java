package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepositoryImpl extends JpaRepository<Url, Hash> {

    @Modifying
    @Transactional

    @Query(value = "DELETE FROM urls WHERE created_at < :timeLimit RETURNING hash", nativeQuery = true)
    List<Hash> deleteExpiredUrlsAndReturnHashes(@Param("timeLimit") LocalDateTime timeLimit);
}
