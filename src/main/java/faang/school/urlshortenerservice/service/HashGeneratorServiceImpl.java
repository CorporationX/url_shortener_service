package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberSequenceRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorServiceImpl implements HashGeneratorService {

    private final HashRepository hashRepository;
    private final UniqueNumberSequenceRepository numberSequenceRepository;
    private final Encoder<Long, Hash> encoder;

    @Value("${server.hash.generate.batch-size}")
    private int generateBatchSize;

    @Override
    @Async("mainThreadPoolExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = numberSequenceRepository.getUniqueNumbers(generateBatchSize);
        List<Hash> hashes = encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
