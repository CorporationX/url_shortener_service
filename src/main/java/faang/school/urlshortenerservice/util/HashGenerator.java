package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${encoder.settings.batchSize}")
    private int batchSize;

    @Async("HashesGeneratorThreadPool")
    public void generateBatch() {
        List<Long> randomNumbersList = hashRepository.getUniqueNumbers(batchSize);

        if (randomNumbersList.isEmpty()) {
            throw new RuntimeException("There are no free Numbers for generating new hashes!");
        }

        List<String> hashList = base62Encoder.encode(randomNumbersList);

        hashRepository.saveAll(hashList);
    }
}
