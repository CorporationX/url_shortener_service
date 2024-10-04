package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Async(value = "hashThreadPool")
    @Transactional
    public void generateBatch() {
        List<Long> uniqueValues = hashRepository.getUniqueValues();
        List<String> hashes = uniqueValues.stream()
                .map(baseEncoder::encode)
                .toList();
        hashRepository.batchSave(hashes);
    }
}
