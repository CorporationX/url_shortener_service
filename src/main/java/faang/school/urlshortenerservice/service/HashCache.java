package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
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

    public String getHash() {
        if (hashQueueManager.shouldRefill()) {
            executorService.submit(hashQueueManager::refillQueueFromData);
            checkAndTriggerHashGeneration();
        }
        return hashQueueManager.pollHash();
    }

    public void checkAndTriggerHashGeneration(){
        int currentCount = hashRepository.getHashesCount();
        if(hashGenerationService.needsHashGeneration(currentCount)){
            executorService.submit(()-> hashGenerationService.generateHash(currentCount));
        }
    }
}
