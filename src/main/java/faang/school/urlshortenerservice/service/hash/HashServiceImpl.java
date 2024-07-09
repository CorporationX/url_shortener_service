package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;

    @Value("${batch-size.hash}")
    private Long batchSize;
    private final Base62Encoder encoder;

    @Override
    @Transactional
    @Async("generateBatchExecutor")
    public void generateBatch() {
        List<Hash> hashes = encoder.encodeHashes(hashRepository.findUniqueNumbers(batchSize)).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}