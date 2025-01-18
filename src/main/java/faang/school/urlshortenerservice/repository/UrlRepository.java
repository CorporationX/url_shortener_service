package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlAssociation, String> {

}
