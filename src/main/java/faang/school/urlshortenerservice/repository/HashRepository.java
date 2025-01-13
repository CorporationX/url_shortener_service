package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
                SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :n) 
            """)
    public List<Long> getUniqueNumbers(long n);

    public List<Hash> getHashBatch();
}
