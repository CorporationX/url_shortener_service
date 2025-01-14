package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    @Value("${hash.generate_size:5000}")
    private long generateSize;
    @Value("${hash.get_size:2700}")
    private long getSize;
    @Value("${hash.min_cache_rep_size:2500}")
    private long minSize;
    @Value("${hash.rep_mid_size:7000}")
    private long hashForSchedulerSize;
    private AtomicBoolean aBoolean = new AtomicBoolean(false);

    @PostConstruct
    public void init(){
        generateHash();
    }

    @Transactional
    @Scheduled(cron = "${hash.cache.generate_hash_time}")
    @Async
    public void generateHash() {
        while (hashRepository.count() <= hashForSchedulerSize) {

            List<Hash> hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(generateSize)).stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashes);
        }
    }

    @Transactional
    public List<Hash> findAndDelete() {
        List<Hash> hashes = hashRepository.findAndDelete(getSize);

        if (hashRepository.count() <= minSize) {
            if (aBoolean.compareAndExchange(false, true)) {
                CompletableFuture.runAsync(this::generateHash).thenRun(() -> aBoolean.set(false));
            }
        }

        return hashes;
    }
}
