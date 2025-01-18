package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    private final AtomicBoolean aBoolean;
    private final ExecutorService executorService;

    @Value("${hash.generate_size:5000}")
    private long generateSize;
    @Value("${hash.get_size:2700}")
    private long getSize;
    @Value("${hash.min_cache_rep_size:2500}")
    private long minSize;
    @Value("${hash.rep_mid_size:7000}")
    private long hashForSchedulerSize;

    public HashGenerator(Base62Encoder base62Encoder, HashRepository hashRepository,
                         @Qualifier("hashGeneratorAtomicBoolean") AtomicBoolean aBoolean,
                         ExecutorService executorService) {
        this.base62Encoder = base62Encoder;
        this.hashRepository = hashRepository;
        this.aBoolean = aBoolean;
        this.executorService=executorService;
    }

    @PostConstruct
    public void init() {
        log.info("init values at db if needed");
        generateHash();
    }

    @Transactional
    @Scheduled(cron = "${hash.cache.generate_hash_time}")
    public void generateHash() {
        log.info("check the database for a non-redundant number of values");
        while (hashRepository.count() <= hashForSchedulerSize) {

            log.info("Not enough values at db , creating new values");
            List<Hash> hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(generateSize)).stream()
                    .map(Hash::new)
                    .toList();

            log.info("save new values as butch");
            hashRepository.saveAll(hashes);
        }
    }

    @Transactional
    public List<Hash> findAndDelete() {
        log.info("getting new hash from db");
        List<Hash> hashes = hashRepository.findAndDelete(getSize);

        if (hashRepository.count() <= minSize) {
            if (aBoolean.compareAndExchange(true, false)) {

                log.info("start generating new hash");
                executorService.execute(()->{

                    log.info("start new Thread to generate hash");
                    generateHash();
                    aBoolean.set(true);

                });
            }
        }

        return hashes;
    }
}
