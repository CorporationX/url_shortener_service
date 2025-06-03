package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashCustomRepositoryImpl implements HashCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final HashProperties properties;

    @Override
    public void saveHashesByBatch(List<String> hashes) {
        int batchSize = 50;
        for (int i = 0; i < hashes.size(); i++) {
            entityManager.persist(new Hash(hashes.get(i)));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public List<String> getHashBatch(Integer batchSize) {
        String sql = """
                DELETE FROM hash
                            WHERE hash.hash IN (
                                SELECT hash FROM hash ORDER BY random() LIMIT ?1
                            )
                            RETURNING hash
                """;

        @SuppressWarnings("unchecked")
        List<String> hashes = entityManager
                .createNativeQuery(sql)
                .setParameter(1,properties.batchsize())
                .getResultList();
        return hashes;
    }
}
