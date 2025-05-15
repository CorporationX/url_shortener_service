package faang.school.urlshortenerservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HashCustomRepositoryImpl implements HashCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveHashesByBatch(List<String> hashes) {
        int batchSize = 50;
        for (int i = 0; i < hashes.size(); i++) {
            entityManager.persist(hashes.get(i));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}
