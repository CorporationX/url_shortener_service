package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at <= current_date - INTERVAL '1 year'
            RETURNING hash
            """)
    List<String> findAndDelete();
}
