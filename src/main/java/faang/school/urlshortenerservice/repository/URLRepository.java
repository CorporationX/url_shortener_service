package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface URLRepository extends JpaRepository<Url, String> {

    @Query(value = """
        DELETE FROM url
	    WHERE url.hash IN (select url.hash from url where url.created_at < :date)
        returning *
        """, nativeQuery = true)
    List<Url> deleteByDate(LocalDateTime date);
}
