package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    @Value("${services.hash.batch.size}")
    private Long batchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Override
    @Transactional
    @Async("generateBatchExecutor")
    public void generateBatch() {
        List<Hash> hashes = base62Encoder.encodeSequence(hashRepository.findUniqueSequence(batchSize)).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
