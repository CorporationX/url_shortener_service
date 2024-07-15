package faang.school.urlshortenerservice.service.batchsaving;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchSaveService {

    @PersistenceContext
    private EntityManager entityManager;

    @Setter
    @Value("${value.setBatchSize}")
    private int batchSize;

    @Transactional
    public <T> void saveEntities(List<String> dataList, Class<T> entityType) {
        log.info("Starting to save {} entities of type {}", dataList.size(), entityType.getSimpleName());

        try {
            Method setterMethod = entityType.getMethod("setHash", String.class);

            IntStream.range(0, dataList.size()).boxed().collect(Collectors.groupingBy(i -> i / batchSize)).values()
                    .forEach(batch -> {
                        log.debug("Processing batch of size {}", batch.size());
                        batch.forEach(i -> {
                            String data = dataList.get(i);
                            try {
                                T entity = entityType.getDeclaredConstructor().newInstance();
                                setterMethod.invoke(entity, data);
                                entityManager.persist(entity);
                                log.debug("Persisted entity: {}", entity);
                            } catch (Exception e) {
                                log.error("Error creating or persisting entity instance", e);
                            }
                        });
                        entityManager.flush();
                        entityManager.clear();
                        log.debug("Flushed and cleared EntityManager for batch");
                    });

            entityManager.flush();
            entityManager.clear();
            log.info("Finished saving entities");

        } catch (NoSuchMethodException e) {
            log.error("The setHash method was not found", e);
            throw new RuntimeException(e);
        }
    }
}
