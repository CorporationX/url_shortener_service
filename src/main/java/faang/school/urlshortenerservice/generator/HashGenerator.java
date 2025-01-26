package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch-size:1000000}")
    private long batchSize;

    @Transactional
    public List<String> getHashList(long quantity) {
        List<String> hashList = hashRepository.getHashesAndDelete(quantity);

        if (hashList.size() < quantity) {
            generateHashList();
            List<String> generatedHashList = hashRepository.getHashesAndDelete(quantity - hashList.size());
            hashList.addAll(generatedHashList);
        }
        return hashList;
    }

    @Async("hashGeneratorTaskExecutor")
    @Transactional
    public void generateHashList() {
        List<Long> range = hashRepository.getNextRangeHashes(batchSize);

        List<Hash> hashList = base62Encoder.encode(range).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashList);
    }
}
