package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashInitializer {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.at-least-amount}")
    private long maxHashCacheSize;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeHashTable() {
        long currentCount = hashRepository.count();
        if (currentCount < maxHashCacheSize) {
            int hashesToGenerate = (int) (maxHashCacheSize - currentCount);
            hashGenerator.generateHashesBatch(hashesToGenerate);
        }
    }
}
