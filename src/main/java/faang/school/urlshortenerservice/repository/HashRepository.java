package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM hash WHERE hash IN (
                                    SELECT hash
                                    FROM hash
                                    ORDER BY hash
                                    LIMIT :limit
                                )
                                RETURNING *
                    """)
    List<String> getHashBatch(@Param("limit") int limit);


    @Query(nativeQuery = true,
            value = """
                        SELECT NEXTVAL('postgres.public.unique_number_seq')
                        FROM generate_series(1, :count)
                    """)
    List<Long> getUniqueNumbers(@Param("count") int count);
}
