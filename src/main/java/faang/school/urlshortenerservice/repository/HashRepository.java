package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = "select nextval('unique_number_seq') from generate_series(1, 1000)")
    List<Long> getUniqueNumbers();

    @Modifying
    @Query(nativeQuery = true,
            value = """ 
            delete from hash
            where hash in ( select hash from hash limit :count  )
            returning hash  
            """)
    List<String> getHashBatch(Integer count);
}
