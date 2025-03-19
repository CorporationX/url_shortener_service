package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashQueueManager hashQueueManager;
    private final HashRepository hashRepository;
    private final HashGenerationService hashGenerationService;
    private final ExecutorService executorService;

    public String getHash(){
        if(hashQueueManager.shouldRefill()){
            executorService.submit(this::safeReillCache);
        }
        return pollHashFromQueue();
    }

    private String pollHashFromQueue() {
        String hash = hashQueueManager.pollHash();
        log.info("Polled hash from queue: {}", hash);
        return hash;
    }

    private void safeReillCache(){
        hashQueueManager.scheduleRefill(() ->{
            int currentCount = hashRepository.getHashesCount();
            hashQueueManager.refillFromDatabase();
            hashGenerationService.generateHash(currentCount);
        });
    }
}
