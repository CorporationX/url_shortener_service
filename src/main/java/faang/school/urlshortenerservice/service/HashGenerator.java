package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.context.HashGeneratorConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashGeneratorConfig hashGeneratorConfig;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorThreadPool")
    public void generateBatch(){
        List<Long> numbers = hashRepository.getUniqueNumbers(hashGeneratorConfig.getUniqueBatch());
        List<String> hashes = base62Encoder.encode(numbers);
        List<Hash> hashEntities = hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();
        hashRepository.save(hashEntities);
    }
}
