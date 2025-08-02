package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class HashGiver {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final Executor hashGeneratorExecutor;

    @Value("${app.hash.batch-size.generation}")
    private int batchSize;
    @Value("${app.hash.percentage-filling}")
    private int percentageFilling;
    @Value("${app.lock-value}")
    private long lockFieldKey;

    public HashGiver(
            HashRepository hashRepository,
            HashGenerator hashGenerator,
            @Qualifier("hashGeneratorExecutor") Executor hashGeneratorExecutor
    ) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.hashGeneratorExecutor = hashGeneratorExecutor;
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getAndDeleteHashBatch(amount);
        boolean lessThanNeeded = hashes.size() < amount;
        if (lessThanNeeded) {
            int missingAmount = amount - hashes.size();
            List<String> hashValues = hashGenerator.generateHashValues(missingAmount);
            hashes.addAll(hashValues);
            generateHashBatchAndSaveInDBAsync();
        }
        if (!lessThanNeeded && requiredFillingTable()) {
            generateHashBatchAndSaveInDBAsync();
        }
        return hashes;
    }

    private boolean requiredFillingTable() {
        long amountInDB = hashRepository.count();
        double currentPercentageFilling = amountInDB * 100.0 / batchSize;
        return currentPercentageFilling <= percentageFilling;
    }

    private void generateHashBatchAndSaveInDBAsync() {
        if (hashRepository.tryAdvisoryLock(lockFieldKey)) {
            hashGeneratorExecutor.execute(() -> {
                try {
                    hashGenerator.generateHashBatchAndSaveInDB();
                } finally {
                    hashRepository.unlockAdvisoryLock(lockFieldKey);
                }
            });
        }
    }
}
