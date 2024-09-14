package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash,String> {

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)")
    public List<Long> getUniqueNumbers(long n);

    @Query(nativeQuery = true, value = """
              DELETE FROM hash
              WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY random()
                LIMIT :amount
              )
              RETURNING *
            """)
    @Modifying
    public List<Hash> getHashBatchAndDelete(long amount);

}
