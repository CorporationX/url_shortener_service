package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.BaseEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerationService {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Async("hashGenerationTaskExecutor")
    public void generateBatch() {
        List<Long> uniqSequence = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> generatedHashes = uniqSequence.stream()
                .map(baseEncoder::encode)
                .map(Hash::new).toList();
        hashRepository.saveBatchHashes(generatedHashes);
    }

}
