package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.context.HashGeneratorConfig;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;
    private final HashGeneratorConfig hashGeneratorConfig;

    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(hashGeneratorConfig.getUniqueBatch());
        List<String> hashes = encoder.encode(numbers);
        List<Hash> newHashes = hashes.stream().map(hash -> Hash.builder().hash(hash).build()).toList();
        hashRepository.saveAll(newHashes);
    }
}