package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query("SELECT COUNT(h) FROM Hash h")
    long countHashes();
}
