package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    @Value("${hash.batchSize:1000}")
    private int batchSize;

    @Transactional
    @Async("generateBatchThreadPool")
    public List<Hash> generateBatch() {
        List<Hash> hashes = hashRepository.getNextRange(batchSize).stream()
                .map(encoder::encode)
                .map(Hash::new)
                .toList();

        return hashRepository.saveAll(hashes);
    }
}
