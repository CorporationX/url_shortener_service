package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryCustomImpl implements HashRepositoryCustom {

    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void saveAllBatched(List<Hash> entities, int batchSize) {
        new TransactionTemplate(transactionManager).execute(status -> {
            for (int i = 0; i < entities.size(); i++) {
                entityManager.persist(entities.get(i));

                if (i % batchSize == 0 && i > 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
            return null;
        });
    }
}