package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Query(
            value = """
                    delete
                    from url
                    where hash in (
                    	select hash
                    	from url
                    	where created_at <= (select current_date - interval '1 year')
                    	for update skip locked
                    	)
                    returning hash
                    """,
            nativeQuery = true)
    List<String> findAndDeleteUnusedHashes();
}
