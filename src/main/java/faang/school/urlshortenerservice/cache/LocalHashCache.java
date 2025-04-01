package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class LocalHashCache {

    private final HashGenerator hashGenerator;
    private final HashGeneratorService hashGeneratorService;
    private final CacheProperties properties;
    private final Queue<String> hashes;
    private final AtomicBoolean isFilling;
    private final int hashesAmountToAdd;

    public LocalHashCache(HashGenerator hashGenerator, HashGeneratorService hashGeneratorService, CacheProperties properties) {
        this.hashGenerator = hashGenerator;
        this.hashGeneratorService = hashGeneratorService;
        this.properties = properties;
        this.hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        this.isFilling = new AtomicBoolean(false);
        this.hashesAmountToAdd = properties.getCapacity() * (100 - properties.getFillPercent()) / 100;
    }

    @PostConstruct
    public void init() {
        List<Hash> hashesList = hashGeneratorService.getHashes(properties.getCapacity());
        List<String> hashesStrList = hashesList.stream()
                .map(Hash::getHash)
                .toList();
        hashes.addAll(hashesStrList);
    }

    public String getHash() {
        if (isCacheNeedToBeFilled() && isFilling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(hashesAmountToAdd)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to fill hashes", exception);
                        } else {
                            hashes.addAll(result);
                        }
                        isFilling.set(false);
                    });
        }
        return hashes.poll();
    }

    private boolean isCacheNeedToBeFilled() {
        int percentageOfFilling = hashes.size() / (properties.getCapacity() / 100);
        return percentageOfFilling < properties.getFillPercent();
    }
}
