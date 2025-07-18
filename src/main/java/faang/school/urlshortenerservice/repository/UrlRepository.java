package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    delete from url 
                        where created_at < current_date - (:daysCount * interval '1 day') 
                        returning hash
                    """
    )
    List<String> retrieveOldUrls(int daysCount);
}
