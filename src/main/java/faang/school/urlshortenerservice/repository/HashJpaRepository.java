package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashJpaRepository extends JpaRepository<Hash, String> {
    @Query(value = "delete from hash where hash IN (select hash from hash order by random() limit :limit) returning *;",
            nativeQuery = true)
    List<String> getHashBatch(@Param("limit") int limit);
}
