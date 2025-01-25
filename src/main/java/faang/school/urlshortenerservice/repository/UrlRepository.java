package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlAssociation, String> {

    @Query(nativeQuery = true, value = """
            DELETE  FROM  url WHERE CURRENT_DATE - created_at >= 365
            RETURNING hash""")
    public List<String> deleteUrlAssociationByTime();

}
