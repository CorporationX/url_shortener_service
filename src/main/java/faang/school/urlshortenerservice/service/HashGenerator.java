package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final Lock lock = new ReentrantLock();

    @Value("${unique-numbers.amount}")
    private int uniqueNumbersAmount;

    @Async(value = "hashTaskExecutor")
    @Transactional
    public void generateBatch() {
        if (lock.tryLock()) {
            try {
                List<Long> uniqueNumbersToEncode = hashRepository.getUniqueNumbers(uniqueNumbersAmount);
                List<String> hashesOfUniqueNumbers = base62Encoder.encode(uniqueNumbersToEncode);

                saveHashes(hashesOfUniqueNumbers);
            } finally {
                lock.unlock();
            }
        } else {
            log.info("Hash generation are already in use");
        }
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        hashRepository.save(hashes);
    }

    @Transactional
    public List<String> getHashes(int count) {
        if (hashRepository.getSize() <= count) {
            generateBatch();
        }

        return hashRepository.getHashes(count).stream()
                .map(Hash::getHash)
                .toList();
    }
}
