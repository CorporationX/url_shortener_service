package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepositoryImpl hashRepositoryImpl;
    private final Base62Encoder encoder;

    @Async("taskExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> emptyIds = hashRepositoryImpl.getUniqueNumbers();

        List<String> hashes = encoder.encodeSequence(emptyIds);
        hashRepositoryImpl.save(hashes);

        log.info("Generated new hash sequence from id: {} to id: {}",
                emptyIds.get(0), emptyIds.get(emptyIds.size() - 1));
    }
}
