package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
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
    @Value("${hash.generation-batch.size}")
    private Long generationBatchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;


    @Async(value = "taskExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> seeds = hashRepository.getNUniqueNumbers(generationBatchSize);
        List<Hash> hashes = encoder.encodeList(seeds);
        hashRepository.saveHashesList(hashes);
    }
}