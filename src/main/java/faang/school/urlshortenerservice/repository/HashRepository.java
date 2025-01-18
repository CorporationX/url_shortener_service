package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(value = "SELECT nextval('unique_number_seq') from generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") long n);

    @Modifying
    @Query(value = "INSERT INTO public.hash(hash) VALUES(:hash)", nativeQuery = true)
    void save(@Param("hash") List<Hash> hashes);

    @Modifying
    @Query(value = """
            DELETE FROM public.hash
            WHERE hash IN (
                SELECT hash FROM public.hash
                LIMIT :batchSize
            )
            RETURNING hash
            """, nativeQuery = true)
    Long getHashBatch(@Param("batchSize") long batchSize);
}
