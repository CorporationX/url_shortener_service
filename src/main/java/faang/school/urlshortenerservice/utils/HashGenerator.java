package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${spring.hash-generator.batch-count}")
    private int batchAmount;

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async("generateBatchThreadPool")
    public void generateBatch() {
        log.info("hash generating");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchAmount);
        List<Hash> hashes = encoder.encode(uniqueNumbers).stream()
                .map(str -> Hash.builder().hash(str).build()).toList();
        hashRepository.saveAll(hashes);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        generateBatch();
    }
}
