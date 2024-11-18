package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
        select nextval('public.unique_number_seq') from generate_series(1, ?1)
        """)
    List<Long> getUniqueNumbers(int n);

    @Query(nativeQuery = true, value = """
        delete from hash d
        where d.hash in (
            select
                h.hash
            from hash h
            limit ?1
        )
        returning *
        """)
    @Modifying
    List<Hash> deleteByIdsAndGet(int batchSize);
}
