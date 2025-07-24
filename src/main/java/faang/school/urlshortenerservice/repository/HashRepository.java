package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.FreeHashPool;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<FreeHashPool, Long> {

    @Query(nativeQuery = true, value = """
            select nextval('unique_number_seq') from generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Query(nativeQuery = true, value = """
            delete from free_hash_pool where hash in (
                select hash from free_hash_pool order by hash limit :amount
                        ) returning *
            """)
    List<String> getHashBatchAndDelete(int amount);
}
