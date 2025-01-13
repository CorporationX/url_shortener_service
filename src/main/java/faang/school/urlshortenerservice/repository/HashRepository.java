package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepository{
    List<Long> getUniqueNumbers(long amount);

    void save(List<Hash> hashes);

//    @Query(nativeQuery = true, value = """
//            DELETE FROM hash WHERE id IN (
//                SELECT id FROM hash ORDER BY id LIMIT :amount
//            ) RETURNING *
//            """)
    List<String> getHashBatch(long amount);
}
