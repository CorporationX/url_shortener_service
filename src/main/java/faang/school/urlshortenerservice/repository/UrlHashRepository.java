package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlHashRepository extends JpaRepository<UrlHash, Long> {
    @Modifying
    @Query(
            nativeQuery = true, value = """
            delete from hash
            where id IN (
                select id from hash limit :amount
            )
            returning *
            """
    )
    List<UrlHash> popAll(int amount);
}
