package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
        select nextval('unique_number_seq') FROM generate_series(1, :range)
        """)
    List<Long> getUniqueNumbers(@Param("range") Long range);

    @Modifying
    @Query(nativeQuery = true, value = """
        delete from hashes
         where hash in (
                 select dd.hash from hashes dd limit :range
               )
        returning *
        """)
    List<String> getPortionOfHashes(@Param("range") Long range);

    @Query(nativeQuery = true, value = """
        select count(*) from hashes
        """)
    Long countAll();
}