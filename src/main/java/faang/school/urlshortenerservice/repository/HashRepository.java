package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = "SELECT h.id FROM hash h WHERE value IS NULL ORDER BY h.id LIMIT ?1")
    List<Long> findNUniqueNumbers(int n); // с этим запросом Hibernate делает n разных запросов в базу, т.к. limit не поддерживается им

    List<Hash> findByValueIsNull(Pageable pageable);
}
