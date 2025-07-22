package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import faang.school.urlshortenerservice.model.UrlJPA;

@Repository
public interface UrlRepository extends JpaRepository<UrlJPA, String> {

}
