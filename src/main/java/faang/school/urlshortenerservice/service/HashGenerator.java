package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.hashGenerator.HashGeneratorConfig;
import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    private final HashGeneratorConfig hashGeneratorConfig;

    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGeneratorConfig.getBatchSize());
        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        List<HashEntity> hashEntities = hashes.stream()
                .map(hash -> HashEntity.builder().hash(hash).build())
                .toList();

        hashRepository.saveAll(hashEntities);
    }
}
