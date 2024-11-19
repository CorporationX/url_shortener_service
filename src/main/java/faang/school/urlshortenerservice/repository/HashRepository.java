package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(Integer count);

    @Query(
            value = """
                    delete from hashes
                    where hash in (
                    	select hash
                    	from hashes
                    	for update skip locked
                    	limit :limit
                    	)
                    returning hash
                    """,
            nativeQuery = true)
    List<String> getAndDeleteHashBatch(int limit);
}
