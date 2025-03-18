package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, HashRepositoryJDBC {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1 ,:n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);
}
