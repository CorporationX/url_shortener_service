package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT NEXTVAL('unique_numbers_seq') FROM GENERATE_SERIES(1, :n)
            """)
    List<Long> getUniqueNumbers(@Param("n") long n);
}
